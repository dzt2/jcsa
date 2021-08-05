package com.jcsa.jcparse.lang.astree.expr.oprt;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * Abstract node for binary expression as:<br>
 * <code>BinaryExpr |--> Expr Operator Expr</code> <br>
 *
 * @author yukimula
 */
public interface AstBinaryExpression extends AstExpression {
	/**
	 * get the expression of left operand
	 *
	 * @return
	 */
	public AstExpression get_loperand();

	/**
	 * get the operator node
	 *
	 * @return
	 */
	public AstOperator get_operator();

	/**
	 * get the expression of right operand
	 *
	 * @return
	 */
	public AstExpression get_roperand();
}
