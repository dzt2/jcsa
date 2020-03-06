package com.jcsa.jcparse.lang.astree.expr.base;

/**
 * <code>LiteralExpr |--> Literal</code>
 * 
 * @author yukimula
 *
 */
public interface AstLiteral extends AstBasicExpression {
	/**
	 * get the string of literal for execution environment
	 * 
	 * @return
	 */
	public String get_literal();
}
