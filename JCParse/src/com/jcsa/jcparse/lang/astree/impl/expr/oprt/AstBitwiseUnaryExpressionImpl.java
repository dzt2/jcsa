package com.jcsa.jcparse.lang.astree.impl.expr.oprt;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBitwiseUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
import com.jcsa.jcparse.lang.lexical.COperator;

public class AstBitwiseUnaryExpressionImpl extends AstUnaryExpressionImpl implements AstBitwiseUnaryExpression {

	public AstBitwiseUnaryExpressionImpl(AstOperator operator, AstExpression operand) throws Exception {
		super(operator, operand);

		if (operator.get_operator() != COperator.bit_not)
			throw new IllegalArgumentException("Invalid operator: " + operator.get_operator());
	}

}
