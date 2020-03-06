package com.jcsa.jcparse.lang.ptoken.impl;

import com.jcsa.jcparse.lang.lexical.CDirective;
import com.jcsa.jcparse.lang.ptoken.PDirectiveToken;

public class PDirectiveTokenImpl extends PTokenImpl implements PDirectiveToken {

	private CDirective dir;

	protected PDirectiveTokenImpl(CDirective dir) {
		super();
		this.dir = dir;
	}

	@Override
	public CDirective get_directive() {
		return dir;
	}

	@Override
	public String toString() {
		return "[directive]{ dir = " + dir + "; }";
	}
}
