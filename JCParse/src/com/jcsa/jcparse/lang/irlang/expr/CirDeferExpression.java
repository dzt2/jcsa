package com.jcsa.jcparse.lang.irlang.expr;

/**
 * defer_expression --> * expression
 * @author yukimula
 *
 */
public interface CirDeferExpression extends CirReferExpression {
	public CirExpression get_address();
	public void set_address(CirExpression address) throws IllegalArgumentException;
}
