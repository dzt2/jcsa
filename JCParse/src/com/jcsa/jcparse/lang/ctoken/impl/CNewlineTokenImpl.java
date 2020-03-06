package com.jcsa.jcparse.lang.ctoken.impl;

import com.jcsa.jcparse.lang.ctoken.CNewlineToken;

public class CNewlineTokenImpl extends CTokenImpl implements CNewlineToken {
	protected CNewlineTokenImpl() {
		super();
	}

	@Override
	public String toString() {
		return "<NL>";
	}
}
