package com.jcsa.jcparse.lang.ctype.impl;

import com.jcsa.jcparse.lang.ctype.CArrayType;
import com.jcsa.jcparse.lang.ctype.CType;

public class CArrayTypeImpl implements CArrayType {

	protected CType etype;
	protected int length;

	protected CArrayTypeImpl(int length, CType etype) throws Exception {
		if (etype == null)
			throw new IllegalArgumentException("invalid etype: null");
		else if (length <= 0)
			throw new IllegalArgumentException("Invalid length: " + length);
		else {
			this.etype = etype;
			this.length = length;
		}
	}

	@Override
	public int length() {
		return length;
	}

	@Override
	public CType get_element_type() {
		return etype;
	}

	@Override
	public boolean is_defined() {
		return etype.is_defined();
	}

	@Override
	public String toString() {
		if(length > 0)
			return "(" + etype.toString() + ")[" + length + "]";
		else return "(" + etype.toString() + ")[]";
	}

	@Override
	public boolean equals(Object val) {
		if (val instanceof CArrayType) {
			return ((CArrayType) val).get_element_type().equals(etype) && ((CArrayType) val).length() == length;
		} else
			return false;
	}
}
