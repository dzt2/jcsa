package com.jcsa.jcparse.lang.astree.impl.expr.oprt;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;

public class AstLogicBinaryExpressionImpl extends AstBinaryExpressionImpl implements AstLogicBinaryExpression {

	public AstLogicBinaryExpressionImpl(AstExpression loperand, AstOperator operator, AstExpression roperand)
			throws Exception {
		super(loperand, operator, roperand);

		switch (operator.get_operator()) {
		case logic_and:
		case logic_or:
			break;
		default:
			throw new IllegalArgumentException("Invalid operator: " + operator.get_operator());
		}
	}

}
