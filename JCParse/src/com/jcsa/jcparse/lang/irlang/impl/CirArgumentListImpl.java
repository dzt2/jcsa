package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;

public class CirArgumentListImpl extends CirNodeImpl implements CirArgumentList {

	protected CirArgumentListImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, false);
	}

	@Override
	public int number_of_arguments() {
		return this.number_of_children();
	}
	@Override
	public CirExpression get_argument(int k) throws IndexOutOfBoundsException {
		return (CirExpression) this.get_child(k);
	}
	@Override
	public void add_argument(CirExpression argument) throws IllegalArgumentException {
		this.add_child((CirNodeImpl) argument);
	}

}
