package com.jcsa.jcparse.lang.ctype.impl;

import com.jcsa.jcparse.lang.ctype.CPointerType;
import com.jcsa.jcparse.lang.ctype.CType;

public class CPointerTypeImpl extends CTypeImpl implements CPointerType {

	protected CType pointed_type;

	protected CPointerTypeImpl(CType type) throws Exception {
		if (type == null)
			throw new IllegalArgumentException("invalid type: null");
		else
			this.pointed_type = type;
	}

	@Override
	public CType get_pointed_type() {
		return pointed_type;
	}

	@Override
	public boolean is_defined() {
		return true;
	}

	@Override
	public String toString() {
		return "(" + pointed_type.toString() + ") *";
	}

	@Override
	public boolean equals(Object val) {
		if (val instanceof CPointerType)
			return pointed_type.equals(((CPointerType) val).get_pointed_type());
		else
			return false;
	}

}
