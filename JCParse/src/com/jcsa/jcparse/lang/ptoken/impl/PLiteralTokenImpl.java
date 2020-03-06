package com.jcsa.jcparse.lang.ptoken.impl;

import com.jcsa.jcparse.lang.ptoken.PLiteralToken;

public class PLiteralTokenImpl extends PTokenImpl implements PLiteralToken {

	private boolean widen;
	private String literal;

	protected PLiteralTokenImpl(boolean widen, String literal) {
		super();
		if (literal == null)
			throw new IllegalArgumentException("Invalid literal: null");
		else {
			this.widen = widen;
			this.literal = literal;
		}
	}

	protected PLiteralTokenImpl(String literal) {
		this(false, literal);
	}

	@Override
	public boolean is_widen() {
		return widen;
	}

	@Override
	public String get_literal() {
		return literal;
	}

	@Override
	public String toString() {
		return "[literal]{ widen = " + widen + "; " + "literal = \"" + literal + "\"; }";
	}
}
