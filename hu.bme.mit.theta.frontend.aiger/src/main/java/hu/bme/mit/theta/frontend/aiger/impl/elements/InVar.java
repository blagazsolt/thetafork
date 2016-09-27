package hu.bme.mit.theta.frontend.aiger.impl.elements;

import java.util.List;

import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.decl.impl.Decls;
import hu.bme.mit.theta.core.expr.Expr;
import hu.bme.mit.theta.core.type.BoolType;
import hu.bme.mit.theta.core.type.impl.Types;

public final class InVar extends HwElement {
	private final VarDecl<BoolType> varDecl;

	public InVar(final int nr, final String token) {
		this(nr, Integer.parseInt(token));
	}

	public InVar(final int nr, final int literal) {
		super(literal / 2);
		varDecl = Decls.Var("I" + nr + "_l" + varId, Types.Bool());
	}

	@Override
	public Expr<? extends BoolType> getExpr(final List<HwElement> elements) {
		return varDecl.getRef();
	}

}