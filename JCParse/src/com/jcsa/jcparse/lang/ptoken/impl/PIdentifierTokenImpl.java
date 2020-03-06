package com.jcsa.jcparse.lang.ptoken.impl;

import com.jcsa.jcparse.lang.ptoken.PIdentifierToken;

public class PIdentifierTokenImpl extends PTokenImpl implements PIdentifierToken {

	/** name of the identifier **/
	private String name;

	protected PIdentifierTokenImpl(String name) {
		super();
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException("Invalid identifier: null");
		else
			this.name = name;
	}

	@Override
	public String get_name() {
		return name;
	}

	@Override
	public String toString() {
		return "[identifier]{ name = \"" + name + "\" }";
	}
}
