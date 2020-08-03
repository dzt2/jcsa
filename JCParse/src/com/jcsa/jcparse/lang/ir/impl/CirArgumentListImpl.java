package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.unit.CirArgumentList;

public class CirArgumentListImpl extends CirNodeImpl implements CirArgumentList {

	protected CirArgumentListImpl(CirTree tree) {
		super(tree);
	}

	@Override
	public int number_of_arguments() { return this.number_of_children(); }

	@Override
	public CirExpression get_argument(int k) throws IndexOutOfBoundsException {
		return (CirExpression) this.get_child(k);
	}

	@Override
	protected CirNode copy_self() {
		return new CirArgumentListImpl(this.get_tree());
	}

}
