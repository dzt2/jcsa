package com.jcsa.jcparse.lang.ctype.impl;

import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CEnumerator;

public class CEnumeratorImpl implements CEnumerator {

	protected CEnumType type;
	protected String literal;
	protected int value;

	protected CEnumeratorImpl(CEnumType type, String literal, int value) throws Exception {
		if (type == null)
			throw new IllegalArgumentException("Invalid type: null");
		else if (literal == null || literal.isEmpty())
			throw new IllegalArgumentException("Invalid literal: null");
		else {
			this.type = type;
			this.literal = literal;
			this.value = value;
		}
	}

	@Override
	public CEnumType get_origin() {
		return type;
	}

	@Override
	public String get_literal() {
		return literal;
	}

	@Override
	public int get_value() {
		return value;
	}

	@Override
	public String toString() {
		return literal + "(" + value + ")";
	}
}
