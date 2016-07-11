package hu.bme.mit.inf.ttmc.analysis.sts.pred;

import static hu.bme.mit.inf.ttmc.core.expr.impl.Exprs.Add;
import static hu.bme.mit.inf.ttmc.core.expr.impl.Exprs.And;
import static hu.bme.mit.inf.ttmc.core.expr.impl.Exprs.Eq;
import static hu.bme.mit.inf.ttmc.core.expr.impl.Exprs.Geq;
import static hu.bme.mit.inf.ttmc.core.expr.impl.Exprs.Imply;
import static hu.bme.mit.inf.ttmc.core.expr.impl.Exprs.Int;
import static hu.bme.mit.inf.ttmc.core.expr.impl.Exprs.Lt;
import static hu.bme.mit.inf.ttmc.core.type.impl.Types.Int;
import static hu.bme.mit.inf.ttmc.formalism.common.decl.impl.Decls2.Var;
import static hu.bme.mit.inf.ttmc.formalism.common.expr.impl.Exprs2.Prime;

import java.util.Collections;

import org.junit.Test;

import hu.bme.mit.inf.ttmc.analysis.algorithm.Abstractor;
import hu.bme.mit.inf.ttmc.analysis.algorithm.ArgPrinter;
import hu.bme.mit.inf.ttmc.analysis.algorithm.impl.AbstractorImpl;
import hu.bme.mit.inf.ttmc.analysis.algorithm.impl.CEGARLoopImpl;
import hu.bme.mit.inf.ttmc.analysis.algorithm.impl.RefutationBasedRefiner;
import hu.bme.mit.inf.ttmc.analysis.algorithm.impl.refineroperator.GlobalPredItpRefinerOperator;
import hu.bme.mit.inf.ttmc.analysis.expl.ExplState;
import hu.bme.mit.inf.ttmc.analysis.pred.GlobalPredPrecision;
import hu.bme.mit.inf.ttmc.analysis.pred.PredDomain;
import hu.bme.mit.inf.ttmc.analysis.pred.PredPrecision;
import hu.bme.mit.inf.ttmc.analysis.pred.PredState;
import hu.bme.mit.inf.ttmc.analysis.refutation.ItpRefutation;
import hu.bme.mit.inf.ttmc.analysis.sts.STSAction;
import hu.bme.mit.inf.ttmc.analysis.sts.STSAnalysisContext;
import hu.bme.mit.inf.ttmc.analysis.sts.STSExprSeqConcretizer;
import hu.bme.mit.inf.ttmc.core.expr.Expr;
import hu.bme.mit.inf.ttmc.core.expr.impl.Exprs;
import hu.bme.mit.inf.ttmc.core.type.BoolType;
import hu.bme.mit.inf.ttmc.core.type.IntType;
import hu.bme.mit.inf.ttmc.formalism.common.decl.VarDecl;
import hu.bme.mit.inf.ttmc.formalism.sts.STS;
import hu.bme.mit.inf.ttmc.formalism.sts.impl.STSImpl;
import hu.bme.mit.inf.ttmc.solver.ItpSolver;
import hu.bme.mit.inf.ttmc.solver.SolverManager;
import hu.bme.mit.inf.ttmc.solver.z3.Z3SolverManager;

public class STSPredTest {

	@Test
	public void test() {

		final VarDecl<IntType> vx = Var("x", Int());
		final Expr<IntType> x = vx.getRef();

		final int mod = 10;

		final Expr<? extends BoolType> init = Eq(x, Int(0));
		final Expr<? extends BoolType> trans = And(Imply(Lt(x, Int(mod)), Eq(Prime(x), Add(x, Int(1)))), Imply(Geq(x, Int(mod)), Eq(Prime(x), Int(0))));
		final Expr<? extends BoolType> target = Eq(x, Int(mod));

		final STS sts = new STSImpl.Builder().addInit(init).addTrans(trans).setProp(Exprs.Not(target)).build();

		final STSAnalysisContext context = new STSAnalysisContext(trans);

		final SolverManager manager = new Z3SolverManager();
		final ItpSolver solver = manager.createItpSolver();

		final PredDomain domain = PredDomain.create(solver);

		final STSPredInitFunction initFunction = new STSPredInitFunction(init, solver);
		final STSPredTransferFunction transferFunction = new STSPredTransferFunction(solver);
		final STSPredTargetPredicate targetPredicate = new STSPredTargetPredicate(target, solver);

		final GlobalPredPrecision precision = GlobalPredPrecision.create(Collections.singleton(Lt(x, Int(mod))));

		final Abstractor<PredState, STSAction, PredPrecision> abstractor = new AbstractorImpl<>(context, domain, initFunction, transferFunction,
				targetPredicate);

		final STSExprSeqConcretizer concretizerOp = new STSExprSeqConcretizer(sts, solver);
		final GlobalPredItpRefinerOperator<STSAction> refinerOp = new GlobalPredItpRefinerOperator<STSAction>();

		final RefutationBasedRefiner<PredState, ExplState, ItpRefutation, GlobalPredPrecision, STSAction> refiner = new RefutationBasedRefiner<>(concretizerOp,
				refinerOp);

		final CEGARLoopImpl<PredState, STSAction, GlobalPredPrecision, ExplState> cegarLoop = new CEGARLoopImpl<>(abstractor, refiner);

		cegarLoop.check(precision);

		System.out.println(ArgPrinter.toGraphvizString(abstractor.getARG()));
		System.out.println(cegarLoop.getStatus());

	}

}
