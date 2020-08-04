package com.jcsa.jcparse.lang.cirlang.expr.refer;

/**
 * <code>
 * 	identifier |-- {identifier}@{scope_key}
 * </code>
 * @author yukimula
 *
 */
public interface CirIdentifierExpression extends CirNameExpression {
	
	/**
	 * @return the identifier of source code
	 */
	public String get_identifier();
	
	/**
	 * @return the integer key of the scope
	 */
	public int get_scope_key();
	
}
