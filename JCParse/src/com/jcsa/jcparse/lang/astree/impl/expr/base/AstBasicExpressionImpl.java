package com.jcsa.jcparse.lang.astree.impl.expr.base;

import com.jcsa.jcparse.lang.astree.expr.base.AstBasicExpression;
import com.jcsa.jcparse.lang.astree.impl.expr.AstFixedExpressionImpl;

public abstract class AstBasicExpressionImpl extends AstFixedExpressionImpl implements AstBasicExpression {

	protected AstBasicExpressionImpl() throws Exception {
		super(0);
	}

}
