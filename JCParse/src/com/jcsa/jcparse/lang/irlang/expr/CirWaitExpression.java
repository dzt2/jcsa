package com.jcsa.jcparse.lang.irlang.expr;

/**
 * wait_expression	|-- wait function
 * @author yukimula
 *
 */
public interface CirWaitExpression extends CirValueExpression {
	public CirExpression get_function();
	public void set_function(CirExpression function) throws IllegalArgumentException;
}
