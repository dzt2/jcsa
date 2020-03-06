package com.jcsa.jcparse.lang.astree.impl.expr.oprt;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;

public class AstIncrePostfixExpressionImpl extends AstPostfixExpressionImpl implements AstIncrePostfixExpression {

	public AstIncrePostfixExpressionImpl(AstExpression operand, AstOperator operator) throws Exception {
		super(operand, operator);

		switch (operator.get_operator()) {
		case increment:
		case decrement:
			break;
		default:
			throw new IllegalArgumentException("Invalid operator: " + operator.get_operator());
		}
	}

}
