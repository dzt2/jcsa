package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirInitializerBody;

public class CirInitializerBodyImpl extends CirExpressionImpl implements CirInitializerBody {

	protected CirInitializerBodyImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, false);
	}

	@Override
	public int number_of_elements() {
		return this.number_of_children();
	}
	@Override
	public CirExpression get_element(int k) throws IndexOutOfBoundsException {
		return (CirExpression) this.get_child(k);
	}

	@Override
	public void add_element(CirExpression element) throws IllegalArgumentException {
		this.add_child((CirNodeImpl) element);
	}

}
