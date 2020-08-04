package com.jcsa.jcparse.lang.cirlang.expr.value;

/**
 * string_literal |-- {literal: String}
 * @author yukimula
 *
 */
public interface CirStringLiteral extends CirValueExpression {
	
	/**
	 * @return the literal of the string node
	 */
	public String get_literal();
	
}
