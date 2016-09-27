package hu.bme.mit.theta.solver.z3.solver;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.microsoft.z3.Status;

import hu.bme.mit.theta.core.expr.Expr;
import hu.bme.mit.theta.core.model.Model;
import hu.bme.mit.theta.core.type.BoolType;
import hu.bme.mit.theta.solver.Solver;
import hu.bme.mit.theta.solver.SolverStatus;
import hu.bme.mit.theta.solver.Stack;
import hu.bme.mit.theta.solver.UnknownSolverStatusException;
import hu.bme.mit.theta.solver.impl.StackImpl;
import hu.bme.mit.theta.solver.z3.trasform.Z3SymbolTable;
import hu.bme.mit.theta.solver.z3.trasform.Z3TermTransformer;
import hu.bme.mit.theta.solver.z3.trasform.Z3TransformationManager;

public class Z3Solver implements Solver {

	private final Z3SymbolTable symbolTable;
	private final Z3TransformationManager transformationManager;
	private final Z3TermTransformer termTransformer;

	private final com.microsoft.z3.Context z3Context;
	private final com.microsoft.z3.Solver z3Solver;

	private final Stack<Expr<? extends BoolType>> assertions;
	private final Map<String, Expr<? extends BoolType>> assumptions;

	private static final String ASSUMPTION_LABEL = "_LABEL_%d";
	private int labelNum = 0;

	private Model model;
	private Collection<Expr<? extends BoolType>> unsatCore;
	private SolverStatus status;

	public Z3Solver(final Z3SymbolTable symbolTable, final Z3TransformationManager transformationManager,
			final Z3TermTransformer termTransformer, final com.microsoft.z3.Context z3Context,
			final com.microsoft.z3.Solver z3Solver) {
		this.symbolTable = symbolTable;
		this.transformationManager = transformationManager;
		this.termTransformer = termTransformer;
		this.z3Context = z3Context;
		this.z3Solver = z3Solver;

		assertions = new StackImpl<>();
		assumptions = new HashMap<>();
	}

	////

	@Override
	public void add(final Expr<? extends BoolType> assertion) {
		checkNotNull(assertion);

		assertions.add(assertion);
		final com.microsoft.z3.BoolExpr term = (com.microsoft.z3.BoolExpr) transformationManager.toTerm(assertion);
		z3Solver.add(term);

		clearState();
	}

	@Override
	public void track(final Expr<? extends BoolType> assertion) {
		checkNotNull(assertion);

		assertions.add(assertion);
		final com.microsoft.z3.BoolExpr term = (com.microsoft.z3.BoolExpr) transformationManager.toTerm(assertion);
		final String label = String.format(ASSUMPTION_LABEL, labelNum++);
		final com.microsoft.z3.BoolExpr labelTerm = z3Context.mkBoolConst(label);

		assumptions.put(label, assertion);

		z3Solver.assertAndTrack(term, labelTerm);

		clearState();
	}

	@Override
	public SolverStatus check() {
		final Status z3Status = z3Solver.check();
		status = transformStatus(z3Status);
		return status;
	}

	private SolverStatus transformStatus(final Status z3Status) {
		switch (z3Status) {
		case SATISFIABLE:
			return SolverStatus.SAT;
		case UNSATISFIABLE:
			return SolverStatus.UNSAT;
		default:
			throw new UnknownSolverStatusException();
		}
	}

	@Override
	public void push() {
		assertions.push();
		z3Solver.push();
	}

	@Override
	public void pop(final int n) {
		assertions.pop(n);
		z3Solver.pop(n);
		clearState();
	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SolverStatus getStatus() {
		return status;
	}

	@Override
	public Model getModel() {
		checkState(status == SolverStatus.SAT);

		if (model == null) {
			model = extractModel();
		}

		assert model != null;
		return model;
	}

	private Model extractModel() {
		assert status == SolverStatus.SAT;
		assert model == null;

		final com.microsoft.z3.Model z3Model = z3Solver.getModel();
		assert z3Model != null;

		return new Z3Model(symbolTable, transformationManager, termTransformer, z3Model);
	}

	@Override
	public Collection<Expr<? extends BoolType>> getUnsatCore() {
		checkState(status == SolverStatus.UNSAT);

		if (unsatCore == null) {
			unsatCore = extractUnsatCore();
		}

		assert unsatCore != null;
		return Collections.unmodifiableCollection(unsatCore);
	}

	private Collection<Expr<? extends BoolType>> extractUnsatCore() {
		assert status == SolverStatus.UNSAT;
		assert unsatCore == null;

		final Collection<Expr<? extends BoolType>> unsatCore = new LinkedList<>();

		final com.microsoft.z3.Expr[] z3UnsatCore = z3Solver.getUnsatCore();

		for (int i = 0; i < z3UnsatCore.length; i = i + 1) {
			final com.microsoft.z3.Expr term = z3UnsatCore[i];

			checkState(term.isConst());

			final String label = term.toString();
			final Expr<? extends BoolType> assumption = assumptions.get(label);

			assert assumption != null;
			unsatCore.add(assumption);
		}

		return unsatCore;
	}

	@Override
	public Collection<Expr<? extends BoolType>> getAssertions() {
		return assertions.toCollection();
	}

	private void clearState() {
		status = null;
		model = null;
		unsatCore = null;
	}

}