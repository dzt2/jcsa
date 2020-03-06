package com.jcsa.jcparse.lang.astree.impl.expr.base;

import com.jcsa.jcparse.lang.astree.expr.base.AstLiteral;

public class AstLiteralImpl extends AstBasicExpressionImpl implements AstLiteral {

	protected String literal;

	public AstLiteralImpl(String literal) throws Exception {
		super();
		if (literal == null)
			throw new IllegalArgumentException("Invalid literal: null");
		else
			this.literal = literal;
	}

	@Override
	public String get_literal() {
		return literal;
	}

}
