package com.jcsa.jcparse.lang.astree.impl.expr.oprt;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstPostfixExpression;
import com.jcsa.jcparse.lang.astree.impl.expr.AstFixedExpressionImpl;

public abstract class AstPostfixExpressionImpl extends AstFixedExpressionImpl implements AstPostfixExpression {

	protected AstPostfixExpressionImpl(AstExpression operand, AstOperator operator) throws Exception {
		super(2);
		this.set_child(0, operand);
		this.set_child(1, operator);
	}

	@Override
	public AstOperator get_operator() {
		return (AstOperator) this.children[1];
	}

	@Override
	public AstExpression get_operand() {
		return (AstExpression) this.children[0];
	}

}
