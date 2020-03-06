package com.jcsa.jcparse.lang.ctype.impl;

import com.jcsa.jcparse.lang.ctype.CQualifierType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.CTypeQualifier;

public class CQualifierTypeImpl implements CQualifierType {

	protected CTypeQualifier qualifier;
	protected CType type;

	protected CQualifierTypeImpl(CTypeQualifier qualifier, CType type) throws Exception {
		if (qualifier == null)
			throw new IllegalArgumentException("Invalid qualifier: null");
		else if (type == null)
			throw new IllegalArgumentException("Invalid type: null");
		else {
			this.qualifier = qualifier;
			this.type = type;
		}
	}

	@Override
	public boolean is_defined() {
		return type.is_defined();
	}

	@Override
	public CTypeQualifier get_qualifier() {
		return qualifier;
	}

	@Override
	public CType get_reference() {
		return type;
	}

	@Override
	public String toString() {
		return qualifier + " (" + type.toString() + ")";
	}

	@Override
	public boolean equals(Object val) {
		if (val instanceof CQualifierType)
			return type.equals(((CQualifierType) val).get_reference());
		else
			return false;
	}
}
