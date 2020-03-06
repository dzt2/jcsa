package com.jcsa.jcparse.lang.ctoken.impl;

import com.jcsa.jcparse.lang.ctoken.CPunctuatorToken;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

public class CPunctuatorTokenImpl extends CTokenImpl implements CPunctuatorToken {

	private CPunctuator punc;

	protected CPunctuatorTokenImpl(CPunctuator punc) {
		super();
		this.punc = punc;
	}

	@Override
	public CPunctuator get_punctuator() {
		return punc;
	}

	@Override
	public String toString() {
		return "<PC>::" + punc.toString();
	}
}
