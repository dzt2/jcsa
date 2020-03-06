package com.jcsa.jcparse.lang.astree.impl.expr.oprt;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstLogicUnaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
import com.jcsa.jcparse.lang.lexical.COperator;

public class AstLogicUnaryExpressionImpl extends AstUnaryExpressionImpl implements AstLogicUnaryExpression {

	public AstLogicUnaryExpressionImpl(AstOperator operator, AstExpression operand) throws Exception {
		super(operator, operand);

		if (operator.get_operator() != COperator.logic_not)
			throw new IllegalArgumentException("Invalid operator: " + operator.get_operator());
	}

}
