package com.jcsa.jcparse.lang.irlang.expr;

/**
 * string_literal --> {string}
 * @author yukimula
 *
 */
public interface CirStringLiteral extends CirValueExpression {
	public String get_literal();
	public void set_literal(String literal) throws IllegalArgumentException;
}
