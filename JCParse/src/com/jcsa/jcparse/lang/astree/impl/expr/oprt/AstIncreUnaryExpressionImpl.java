package com.jcsa.jcparse.lang.astree.impl.expr.oprt;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;

public class AstIncreUnaryExpressionImpl extends AstUnaryExpressionImpl implements AstIncreUnaryExpression {

	public AstIncreUnaryExpressionImpl(AstOperator operator, AstExpression operand) throws Exception {
		super(operator, operand);

		switch (operator.get_operator()) {
		case increment:
		case decrement:
			break;
		default:
			throw new IllegalArgumentException("Invalid operator: " + operator.get_operator());
		}
	}

}
