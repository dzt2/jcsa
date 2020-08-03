package com.jcsa.jcparse.lang.ir.expr.refer;

/**
 * {ast_key}
 * @author yukimula
 *
 */
public interface CirTemporalReference extends CirNameExpression {
	
	/**
	 * @return the integer key that the temporal variable is generated from
	 */
	public int get_ast_key();
	
}
