package com.jcsa.jcparse.lang.astree.impl.expr.oprt;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
import com.jcsa.jcparse.lang.astree.impl.expr.AstFixedExpressionImpl;

public abstract class AstBinaryExpressionImpl extends AstFixedExpressionImpl implements AstBinaryExpression {

	protected AstBinaryExpressionImpl(AstExpression loperand, AstOperator operator, AstExpression roperand)
			throws Exception {
		super(3);
		this.set_child(0, loperand);
		this.set_child(1, operator);
		this.set_child(2, roperand);
	}

	@Override
	public AstExpression get_loperand() {
		return (AstExpression) this.children[0];
	}

	@Override
	public AstOperator get_operator() {
		return (AstOperator) this.children[1];
	}

	@Override
	public AstExpression get_roperand() {
		return (AstExpression) this.children[2];
	}

}
