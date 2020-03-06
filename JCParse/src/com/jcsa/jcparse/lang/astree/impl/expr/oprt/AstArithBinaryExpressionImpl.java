package com.jcsa.jcparse.lang.astree.impl.expr.oprt;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstArithBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;

public class AstArithBinaryExpressionImpl extends AstBinaryExpressionImpl implements AstArithBinaryExpression {

	public AstArithBinaryExpressionImpl(AstExpression loperand, AstOperator operator, AstExpression roperand)
			throws Exception {
		super(loperand, operator, roperand);

		switch (operator.get_operator()) {
		case arith_add:
		case arith_sub:
		case arith_mul:
		case arith_div:
		case arith_mod:
			break;
		default:
			throw new IllegalArgumentException("Invalid operator: " + operator.get_operator());
		}
	}

}
