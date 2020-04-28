package com.jcsa.jcparse.lang.symbol.impl;

import com.jcsa.jcparse.lang.symbol.SymField;

public class SymFieldImpl extends SymNodeImpl implements SymField {
	
	private String name;
	protected SymFieldImpl(String name) throws IllegalArgumentException {
		if(name == null || name.isBlank())
			throw new IllegalArgumentException("Invalid name: null");
		else {
			this.name = name;
		}
	}
	@Override
	public String get_field_name() { return this.name; }
	
	@Override
	public String toString() {
		return this.name;
	}
}
