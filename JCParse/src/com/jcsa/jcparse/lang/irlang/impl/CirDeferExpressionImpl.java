package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirDeferExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

public class CirDeferExpressionImpl extends CirExpressionImpl implements CirDeferExpression {

	protected CirDeferExpressionImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
	}

	@Override
	public CirExpression get_address() {
		if(this.number_of_children() < 1) {
			return null;
		}
		else {
			return (CirExpression) this.get_child(0);
		}
	}
	@Override
	public void set_address(CirExpression address) throws IllegalArgumentException {
		if(this.number_of_children() == 0) {
			this.add_child((CirNodeImpl) address);
		}
		else {
			throw new IllegalArgumentException("Out of bounds: " + this.number_of_children());
		}
	}

}
