package hu.bme.mit.theta.core.type.bvtype;

import hu.bme.mit.theta.core.model.Valuation;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.abstracttype.DivExpr;
import hu.bme.mit.theta.core.utils.BvUtils;

import java.math.BigInteger;

import static hu.bme.mit.theta.core.type.bvtype.BvExprs.Bv;
import static hu.bme.mit.theta.core.utils.TypeUtils.cast;

public class BvDivExpr extends DivExpr<BvType> {
    private static final int HASH_SEED = 9832;

    private static final String OPERATOR_LABEL = "div";

    private BvDivExpr(final Expr<BvType> leftOp, final Expr<BvType> rightOp) {
        super(leftOp, rightOp);
    }

    public static BvDivExpr of(final Expr<BvType> leftOp, final Expr<BvType> rightOp) {
        return new BvDivExpr(leftOp, rightOp);
    }

    public static BvDivExpr create(final Expr<?> leftOp, final Expr<?> rightOp) {
        final Expr<BvType> newLeftOp = cast(leftOp, (BvType) leftOp.getType());
        final Expr<BvType> newRightOp = cast(rightOp, (BvType) leftOp.getType());
        return BvDivExpr.of(newLeftOp, newRightOp);
    }

    @Override
    public BvType getType() {
        return getOps().get(0).getType();
    }

    @Override
    public BvLitExpr eval(final Valuation val) {
        final BvLitExpr leftOpVal = (BvLitExpr) getLeftOp().eval(val);
        final BvLitExpr rightOpVal = (BvLitExpr) getRightOp().eval(val);

        BigInteger div = BvUtils.bvLitExprToBigInteger(leftOpVal).divide(BvUtils.bvLitExprToBigInteger(rightOpVal));
        div = BvUtils.fitBigIntegerIntoDomain(div, getType().getSize(), getType().isSigned());
        return BvUtils.bigIntegerToBvLitExpr(div, getType().getSize(), getType().isSigned());
    }

    @Override
    public BvDivExpr with(final Expr<BvType> leftOp, final Expr<BvType> rightOp) {
        if (leftOp == getLeftOp() && rightOp == getRightOp()) {
            return this;
        } else {
            return BvDivExpr.of(leftOp, rightOp);
        }
    }

    @Override
    public BvDivExpr withLeftOp(final Expr<BvType> leftOp) {
        return with(leftOp, getRightOp());
    }

    @Override
    public BvDivExpr withRightOp(final Expr<BvType> rightOp) {
        return with(getLeftOp(), rightOp);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof BvDivExpr) {
            final BvDivExpr that = (BvDivExpr) obj;
            return this.getLeftOp().equals(that.getLeftOp()) && this.getRightOp().equals(that.getRightOp());
        } else {
            return false;
        }
    }

    @Override
    protected int getHashSeed() {
        return HASH_SEED;
    }

    @Override
    public String getOperatorLabel() {
        return OPERATOR_LABEL;
    }
}
