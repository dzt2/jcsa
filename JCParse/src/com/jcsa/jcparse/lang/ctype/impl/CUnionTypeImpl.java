package com.jcsa.jcparse.lang.ctype.impl;

import com.jcsa.jcparse.lang.ctype.CFieldBody;
import com.jcsa.jcparse.lang.ctype.CUnionType;

public class CUnionTypeImpl implements CUnionType {
	
	private String name;
	protected CFieldBody body;

	protected CUnionTypeImpl(String name) {
		if(name == null) this.name = "";
		else this.name = name;
		this.body = new CFieldBodyImpl();
	}

	@Override
	public CFieldBody get_fields() {
		return body;
	}

	@Override
	public boolean is_defined() {
		return body.size() > 0;
	}

	@Override
	public String toString() {
		return "union";
	}

	@Override
	public boolean equals(Object val) {
		return this == val;
	}

	@Override
	public String get_name() {
		return name;
	}

	
	@Override
	public String generate_code() {
		if(this.name.isEmpty()) {
			return "union@" + this.hashCode();
		}
		else {
			return "union " + this.name;
		}
	}
	
}
