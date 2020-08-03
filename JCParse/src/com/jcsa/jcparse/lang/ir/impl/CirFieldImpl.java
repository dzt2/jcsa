package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.unit.CirField;

public class CirFieldImpl extends CirNodeImpl implements CirField {
	
	private String name;

	protected CirFieldImpl(CirTree tree, String name) {
		super(tree);
		this.name = name;
	}

	@Override
	public String get_name() { return this.name; }

	@Override
	protected CirNode copy_self() {
		return new CirFieldImpl(this.get_tree(), this.name);
	}

}
