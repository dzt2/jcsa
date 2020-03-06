package com.jcsa.jcparse.lang.ptoken.impl;

import com.jcsa.jcparse.lang.lexical.CPunctuator;
import com.jcsa.jcparse.lang.ptoken.PPunctuatorToken;

public class PPunctuatorTokenImpl extends PTokenImpl implements PPunctuatorToken {

	private CPunctuator punctuator;

	protected PPunctuatorTokenImpl(CPunctuator punc) {
		super();
		this.punctuator = punc;
	}

	@Override
	public CPunctuator get_punctuator() {
		return punctuator;
	}

	@Override
	public String toString() {
		return "[punctuator]{ punc = " + punctuator + "; }";
	}
}
