package com.jcsa.jcparse.lang.astree.expr.oprt;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code>UnaryExpr |--> Operator Expr </code>
 * 
 * @author yukimula
 *
 */
public interface AstUnaryExpression extends AstExpression {
	public AstOperator get_operator();

	public AstExpression get_operand();
}
