package com.jcsa.jcparse.lang.cirlang.expr.refer;

/**
 * <code>
 * 	return_refer_expression |-- return@{scope_key}
 * </code>
 * @author yukimula
 *
 */
public interface CirReturnReferExpression extends CirNameExpression {
	
	/**
	 * @return the integer key of the scope
	 */
	public int get_scope_key();
	
}
