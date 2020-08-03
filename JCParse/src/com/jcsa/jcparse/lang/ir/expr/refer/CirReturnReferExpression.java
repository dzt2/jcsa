package com.jcsa.jcparse.lang.ir.expr.refer;

/**
 * return_ref_expression	|--	return @ scope_id
 * @author yukimula
 *
 */
public interface CirReturnReferExpression extends CirNameExpression {

	/**
	 * @return id of the scope where the name is declared
	 */
	public int get_scope_id();
	
}
