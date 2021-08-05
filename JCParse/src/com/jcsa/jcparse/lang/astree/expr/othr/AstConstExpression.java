package com.jcsa.jcparse.lang.astree.expr.othr;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * Constant expression is that of value is number and can be determined during
 * compilation
 *
 * @author yukimula
 */
public interface AstConstExpression extends AstExpression {
	/**
	 * get the expression it defined
	 *
	 * @return
	 */
	public AstExpression get_expression();
}
