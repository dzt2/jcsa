package com.jcsa.jcparse.lang.astree.impl.expr.oprt;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPointUnaryExpression;

public class AstPointUnaryExpressionImpl extends AstUnaryExpressionImpl implements AstPointUnaryExpression {

	public AstPointUnaryExpressionImpl(AstOperator operator, AstExpression operand) throws Exception {
		super(operator, operand);

		switch (operator.get_operator()) {
		case address_of:
		case dereference:
			break;
		default:
			throw new IllegalArgumentException("Invalid operator: " + operator.get_operator());
		}
	}

}
