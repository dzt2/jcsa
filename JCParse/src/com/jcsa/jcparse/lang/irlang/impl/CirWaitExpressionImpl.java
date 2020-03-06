package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirWaitExpression;

public class CirWaitExpressionImpl extends CirExpressionImpl implements CirWaitExpression {

	protected CirWaitExpressionImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
	}

	@Override
	public CirExpression get_function() {
		if(this.number_of_children() < 1) {
			return null;
		}
		else {
			return (CirExpression) this.get_child(0);
		}
	}
	@Override
	public void set_function(CirExpression function) throws IllegalArgumentException {
		if(this.number_of_children() == 0) {
			this.add_child((CirNodeImpl) function);
		}
		else {
			throw new IllegalArgumentException("Out of bounds: " + this.number_of_children());
		}
	}

}
