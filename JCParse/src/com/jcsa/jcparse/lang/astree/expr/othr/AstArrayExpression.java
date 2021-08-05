package com.jcsa.jcparse.lang.astree.expr.othr;

import com.jcsa.jcparse.lang.astree.base.AstPunctuator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code>ArrayExpr |--> Expr [ Expr ]</code>
 *
 * @author yukimula
 */
public interface AstArrayExpression extends AstExpression {
	/**
	 * get expression as array
	 *
	 * @return
	 */
	public AstExpression get_array_expression();

	/**
	 * get [
	 *
	 * @return
	 */
	public AstPunctuator get_left_bracket();

	/**
	 * get expression as dimension
	 *
	 * @return
	 */
	public AstExpression get_dimension_expression();

	/**
	 * get ]
	 *
	 * @return
	 */
	public AstPunctuator get_right_bracket();
}
