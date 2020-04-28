package com.jcsa.jcparse.lang.symbol.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.symbol.SymStringLiteral;

public class SymStringLiteralImpl extends SymExpressionImpl implements SymStringLiteral {
	
	private String literal;
	protected SymStringLiteralImpl(CType data_type, String literal) throws IllegalArgumentException {
		super(data_type);
		if(literal == null)
			throw new IllegalArgumentException("Invalid literal: null");
		else { this.literal = literal; }
	}
	
	@Override
	public String get_literal() { return this.literal; }
	
	@Override
	public String toString() {
		return "#\"" + this.literal + "\"";
	}
	
}
