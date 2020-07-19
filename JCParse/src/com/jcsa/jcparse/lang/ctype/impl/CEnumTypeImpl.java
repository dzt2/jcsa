package com.jcsa.jcparse.lang.ctype.impl;

import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CEnumeratorList;

public class CEnumTypeImpl extends CTypeImpl implements CEnumType {

	private String name;
	protected CEnumeratorList elist;

	protected CEnumTypeImpl(String name) {
		if(name == null) this.name = "";
		else this.name = name;
		elist = new CEnumeratorListImpl();
	}

	@Override
	public CEnumeratorList get_enumerator_list() {
		return elist;
	}

	@Override
	public boolean is_defined() {
		return elist.size() != 0;
	}

	@Override
	public boolean equals(Object val) {
		return val == this;
	}

	@Override
	public String toString() {
		return "enum";
	}

	@Override
	public String get_name() {
		return name;
	}
	
}
