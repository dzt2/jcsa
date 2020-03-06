package com.jcsa.jcparse.lang.astree.impl.expr.oprt;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.astree.impl.expr.AstFixedExpressionImpl;

public abstract class AstUnaryExpressionImpl extends AstFixedExpressionImpl implements AstUnaryExpression {

	protected AstUnaryExpressionImpl(AstOperator operator, AstExpression operand) throws Exception {
		super(2);
		this.set_child(0, operator);
		this.set_child(1, operand);
	}

	@Override
	public AstOperator get_operator() {
		return (AstOperator) this.children[0];
	}

	@Override
	public AstExpression get_operand() {
		return (AstExpression) this.children[1];
	}

}
