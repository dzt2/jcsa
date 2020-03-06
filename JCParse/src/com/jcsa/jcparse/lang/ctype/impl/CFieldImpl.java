package com.jcsa.jcparse.lang.ctype.impl;

import com.jcsa.jcparse.lang.ctype.CField;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;

public class CFieldImpl implements CField {

	protected CType origin;
	protected String name;
	protected CType vtype;
	protected int bitsize;

	protected CFieldImpl(CStructType origin, String name, CType vtype) throws Exception {
		if (origin == null)
			throw new IllegalArgumentException("Invalid origin: null");
		else if (name == null || name.isEmpty())
			throw new IllegalArgumentException("Invalid name: null");
		else if (vtype == null)
			throw new IllegalArgumentException("Invalid value-type: null");
		else {
			this.origin = origin;
			this.name = name;
			this.vtype = vtype;
			this.bitsize = -1;
		}
	}

	protected CFieldImpl(CUnionType origin, String name, CType vtype) throws Exception {
		if (origin == null)
			throw new IllegalArgumentException("Invalid origin: null");
		else if (name == null || name.isEmpty())
			throw new IllegalArgumentException("Invalid name: null");
		else if (vtype == null)
			throw new IllegalArgumentException("Invalid value-type: null");
		else {
			this.origin = origin;
			this.name = name;
			this.vtype = vtype;
			this.bitsize = -1;
		}
	}

	protected CFieldImpl(CStructType origin, String name, CType vtype, int bitsize) throws Exception {
		if (origin == null)
			throw new IllegalArgumentException("Invalid origin: null");
		else if (name == null || name.isEmpty())
			throw new IllegalArgumentException("Invalid name: null");
		else if (vtype == null)
			throw new IllegalArgumentException("Invalid value-type: null");
		else if (bitsize < 0)
			throw new IllegalArgumentException("Invalid bitsize: " + bitsize);
		else {
			this.origin = origin;
			this.name = name;
			this.vtype = vtype;
			this.bitsize = bitsize;
		}
	}

	protected CFieldImpl(CUnionType origin, String name, CType vtype, int bitsize) throws Exception {
		if (origin == null)
			throw new IllegalArgumentException("Invalid origin: null");
		else if (name == null || name.isEmpty())
			throw new IllegalArgumentException("Invalid name: null");
		else if (vtype == null)
			throw new IllegalArgumentException("Invalid value-type: null");
		else if (bitsize < 0)
			throw new IllegalArgumentException("Invalid bitsize: " + bitsize);
		else {
			this.origin = origin;
			this.name = name;
			this.vtype = vtype;
			this.bitsize = bitsize;
		}
	}

	@Override
	public CType get_origin() {
		return origin;
	}

	@Override
	public String get_name() {
		return name;
	}

	@Override
	public CType get_type() {
		return vtype;
	}

	@Override
	public int get_bitsize() {
		return bitsize;
	}

	@Override
	public String toString() {
		return name;
	}

}
