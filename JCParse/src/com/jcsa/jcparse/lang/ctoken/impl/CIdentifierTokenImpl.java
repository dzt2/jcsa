package com.jcsa.jcparse.lang.ctoken.impl;

import com.jcsa.jcparse.lang.ctoken.CIdentifierToken;

public class CIdentifierTokenImpl extends CTokenImpl implements CIdentifierToken {

	/** name of identifier token **/
	private String name;

	protected CIdentifierTokenImpl(String name) {
		super();
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException("Invalid name: null");
		else
			this.name = name;
	}

	@Override
	public String get_name() {
		return name;
	}

	@Override
	public String toString() {
		return "<ID>::" + name;
	}
}
