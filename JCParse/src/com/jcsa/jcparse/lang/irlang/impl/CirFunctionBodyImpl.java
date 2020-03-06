package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionBody;

public class CirFunctionBodyImpl extends CirNodeImpl implements CirFunctionBody {

	protected CirFunctionBodyImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
	}

	@Override
	public int number_of_statements() { return this.number_of_children(); }
	@Override
	public CirStatement get_statement(int k) throws IndexOutOfBoundsException {
		return (CirStatement) this.get_child(k);
	}
	@Override
	public void add_statement(CirStatement statement) throws IllegalArgumentException {
		this.add_child((CirNodeImpl) statement);
	}
	
}
