package com.jcsa.jcparse.lang.astree.impl.expr.oprt;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;

public class AstBitwiseAssignExpressionImpl extends AstBinaryExpressionImpl implements AstBitwiseAssignExpression {

	public AstBitwiseAssignExpressionImpl(AstExpression loperand, AstOperator operator, AstExpression roperand)
			throws Exception {
		super(loperand, operator, roperand);

		switch (operator.get_operator()) {
		case bit_and_assign:
		case bit_or_assign:
		case bit_xor_assign:
			break;
		default:
			throw new IllegalArgumentException("Invalid operator: " + operator.get_operator());
		}
	}

}
