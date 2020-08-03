package com.jcsa.jcparse.lang.ir.expr.refer;

/**
 * return@{scope_id}
 * @author yukimula
 *
 */
public interface CirReturnReference extends CirNameExpression {
	
	/**
	 * @return the integer that the declarator refers to
	 */
	public int get_scope_id();
	
}
