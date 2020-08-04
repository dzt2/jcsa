package com.jcsa.jcparse.lang.cirlang.expr.refer;

/**
 * <code>
 * 	temporary_expression |-- @{scope_key}
 * </code>
 * @author yukimula
 *
 */
public interface CirTemporaryExpression extends CirNameExpression {
	
	/**
	 * @return the integer key of the scope
	 */
	public int get_scope_key();
	
}
