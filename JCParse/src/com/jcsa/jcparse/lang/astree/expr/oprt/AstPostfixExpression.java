package com.jcsa.jcparse.lang.astree.expr.oprt;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * <code>PostExpr |--> Expr Operator</code>
 *
 * @author yukimula
 *
 */
public interface AstPostfixExpression extends AstExpression {
	public AstOperator get_operator();

	public AstExpression get_operand();
}
