package com.jcsa.jcparse.lang.astree.impl.expr.oprt;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstAssignExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
import com.jcsa.jcparse.lang.lexical.COperator;

public class AstAssignExpressionImpl extends AstBinaryExpressionImpl implements AstAssignExpression {

	public AstAssignExpressionImpl(AstExpression loperand, AstOperator operator, AstExpression roperand)
			throws Exception {
		super(loperand, operator, roperand);

		if (operator.get_operator() != COperator.assign)
			throw new IllegalArgumentException("Invalid operator: " + operator.get_operator());
	}

}
