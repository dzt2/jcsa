package com.jcsa.jcparse.lang.astree.impl.expr.oprt;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;

public class AstArithUnaryExpressionImpl extends AstUnaryExpressionImpl implements AstArithUnaryExpression {

	public AstArithUnaryExpressionImpl(AstOperator operator, AstExpression operand) throws Exception {
		super(operator, operand);

		switch (operator.get_operator()) {
		case positive:
		case negative:
			break;
		default:
			throw new IllegalArgumentException("Invalid operator: " + operator.get_operator());
		}
	}

}
