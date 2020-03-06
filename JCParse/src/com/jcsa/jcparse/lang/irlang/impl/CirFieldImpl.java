package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirField;

public class CirFieldImpl extends CirNodeImpl implements CirField {

	protected CirFieldImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
	}
	
	private String name;
	@Override
	public String get_name() { return this.name; }
	@Override
	public void set_name(String name) throws IllegalArgumentException {
		if(name == null || name.trim().isEmpty())
			throw new IllegalArgumentException("invalid name: null");
		else this.name = name;
	}

}
