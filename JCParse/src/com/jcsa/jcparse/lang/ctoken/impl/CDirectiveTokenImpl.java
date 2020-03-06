package com.jcsa.jcparse.lang.ctoken.impl;

import com.jcsa.jcparse.lang.ctoken.CDirectiveToken;
import com.jcsa.jcparse.lang.lexical.CDirective;

public class CDirectiveTokenImpl extends CTokenImpl implements CDirectiveToken {

	private CDirective directive;

	protected CDirectiveTokenImpl(CDirective dir) {
		super();
		this.directive = dir;
	}

	@Override
	public CDirective get_directive() {
		return directive;
	}

	@Override
	public String toString() {
		return "<DR>::#" + directive.toString().substring(5);
	}
}
