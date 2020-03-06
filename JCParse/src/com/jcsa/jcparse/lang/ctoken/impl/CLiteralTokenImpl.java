package com.jcsa.jcparse.lang.ctoken.impl;

import com.jcsa.jcparse.lang.ctoken.CLiteralToken;

public class CLiteralTokenImpl extends CTokenImpl implements CLiteralToken {

	private boolean widen;
	private String literal;

	protected CLiteralTokenImpl(boolean widen, String literal) {
		super();
		if (literal == null)
			throw new IllegalArgumentException("Invalid literal: null");
		else {
			this.widen = widen;
			this.literal = literal;
		}
	}

	@Override
	public boolean is_widen() {
		return widen;
	}

	@Override
	public String get_exec_literal() {
		return literal;
	}

	@Override
	public String toString() {
		if (widen)
			return "<LT>::w\"" + literal + "\"";
		else
			return "<LT>::\"" + literal + "\"";
	}
}
