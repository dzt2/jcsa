package com.jcsa.jcparse.lang.astree.impl.expr.oprt;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstRelationExpression;

public class AstRelationExpressionImpl extends AstBinaryExpressionImpl implements AstRelationExpression {

	public AstRelationExpressionImpl(AstExpression loperand, AstOperator operator, AstExpression roperand)
			throws Exception {
		super(loperand, operator, roperand);

		switch (operator.get_operator()) {
		case smaller_tn:
		case smaller_eq:
		case equal_with:
		case not_equals:
		case greater_tn:
		case greater_eq:
			break;
		default:
			throw new IllegalArgumentException("Invalid operator: " + operator.get_operator());
		}
	}

}
