package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirStringLiteral;

public class CirStringLiteralImpl extends CirExpressionImpl implements CirStringLiteral {

	protected CirStringLiteralImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
	}

	private String literal;
	@Override
	public String get_literal() { return this.literal; }
	@Override
	public void set_literal(String literal) throws IllegalArgumentException {
		if(literal == null) {
			throw new IllegalArgumentException("Invalid literal: null");
		}
		else {
			this.literal = literal;
		}
	}

}
