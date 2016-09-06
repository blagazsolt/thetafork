package hu.bme.mit.inf.theta.core.type;


public interface FuncType<ParamType extends Type, ResultType extends Type> extends Type {

	public ParamType getParamType();
	public ResultType getResultType();
}