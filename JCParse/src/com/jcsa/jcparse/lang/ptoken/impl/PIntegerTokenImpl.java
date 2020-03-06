package com.jcsa.jcparse.lang.ptoken.impl;

import com.jcsa.jcparse.lang.lexical.CNumberEncode;
import com.jcsa.jcparse.lang.ptoken.PIntegerToken;

public class PIntegerTokenImpl extends PTokenImpl implements PIntegerToken {

	private CNumberEncode encode;
	private String int_literal;
	private String int_suffix;

	protected PIntegerTokenImpl(CNumberEncode encode, String literal, String suffix) {
		super();
		if (literal == null || literal.isEmpty())
			throw new IllegalArgumentException("Invalid int_literal: null");
		else if (suffix == null)
			throw new IllegalArgumentException("Invalid int_suffix: null");
		else {
			this.encode = encode;
			this.int_literal = literal;
			this.int_suffix = suffix;
		}
	}

	protected PIntegerTokenImpl(CNumberEncode encode, String literal) {
		this(encode, literal, "");
	}

	@Override
	public CNumberEncode get_encode() {
		return encode;
	}

	@Override
	public String get_int_literal() {
		return int_literal;
	}

	@Override
	public String get_int_suffix() {
		return int_suffix;
	}

	@Override
	public String toString() {
		return "[integer]{ encode = " + encode + "; " + "literal = \"" + int_literal + "\"; " + "suffix = \'"
				+ int_suffix + "\'; }";
	}
}
