package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirType;

public class CirTypeImpl extends CirNodeImpl implements CirType {

	protected CirTypeImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
	}

	private CType typename;
	@Override
	public CType get_typename() { return this.typename; }
	@Override
	public void set_typename(CType type) throws IllegalArgumentException {
		if(type == null)
			throw new IllegalArgumentException("invalid type: null");
		else this.typename = type;
	}

}
