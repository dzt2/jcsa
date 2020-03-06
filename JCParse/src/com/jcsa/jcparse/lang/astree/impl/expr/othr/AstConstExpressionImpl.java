package com.jcsa.jcparse.lang.astree.impl.expr.othr;

import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.othr.AstConstExpression;
import com.jcsa.jcparse.lang.astree.impl.expr.AstFixedExpressionImpl;

public class AstConstExpressionImpl extends AstFixedExpressionImpl implements AstConstExpression {

	public AstConstExpressionImpl(AstExpression expression) throws Exception {
		super(1);
		if (expression == null)
			throw new IllegalArgumentException("Invalid expression: null");
		else
			this.set_child(0, expression);
	}

	@Override
	public AstExpression get_expression() {
		return (AstExpression) this.children[0];
	}

}
