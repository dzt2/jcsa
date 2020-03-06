package com.jcsa.jcparse.lang.ptoken.impl;

import com.jcsa.jcparse.lang.ptoken.PCharacterToken;

public class PCharacterTokenImpl extends PTokenImpl implements PCharacterToken {

	private boolean widen;
	private String c_sequence;

	protected PCharacterTokenImpl(boolean widen, String c_seq) {
		super();
		if (c_seq == null || c_seq.isEmpty())
			throw new IllegalArgumentException("Invalid c-char-sequence: null");
		else {
			this.widen = widen;
			this.c_sequence = c_seq;
		}
	}

	protected PCharacterTokenImpl(String c_seq) {
		this(false, c_seq);
	}

	@Override
	public String get_char_sequence() {
		return c_sequence;
	}

	@Override
	public boolean is_widen() {
		return widen;
	}

	@Override
	public String toString() {
		return "[character]{ widen = " + widen + "; sequence = \"" + c_sequence + "\"; }";
	}
}
