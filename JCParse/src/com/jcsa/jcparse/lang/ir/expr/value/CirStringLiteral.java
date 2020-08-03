package com.jcsa.jcparse.lang.ir.expr.value;

/**
 * string_literal |-- {literal: String}
 * @author yukimula
 *
 */
public interface CirStringLiteral extends CirValueExpression {
	
	/**
	 * @return the string literal of the node
	 */
	public String get_literal();
	
}
