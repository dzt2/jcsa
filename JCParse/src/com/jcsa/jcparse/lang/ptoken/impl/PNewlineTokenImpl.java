package com.jcsa.jcparse.lang.ptoken.impl;

import com.jcsa.jcparse.lang.ptoken.PNewlineToken;

public class PNewlineTokenImpl extends PTokenImpl implements PNewlineToken {
	protected PNewlineTokenImpl() {
		super();
	}

	@Override
	public String toString() {
		return "[Newline]";
	}
}
