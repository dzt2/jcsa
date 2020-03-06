package com.jcsa.jcparse.lang.astree.impl.expr.oprt;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstShiftBinaryExpression;

public class AstShiftBinaryExpressionImpl extends AstBinaryExpressionImpl implements AstShiftBinaryExpression {

	public AstShiftBinaryExpressionImpl(AstExpression loperand, AstOperator operator, AstExpression roperand)
			throws Exception {
		super(loperand, operator, roperand);

		switch (operator.get_operator()) {
		case left_shift:
		case righ_shift:
			break;
		default:
			throw new IllegalArgumentException("Invalid operator: " + operator.get_operator());
		}
	}

}
