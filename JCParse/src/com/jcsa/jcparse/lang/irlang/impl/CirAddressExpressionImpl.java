package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirAddressExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;

public class CirAddressExpressionImpl extends CirExpressionImpl implements CirAddressExpression {

	protected CirAddressExpressionImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
	}

	@Override
	public CirReferExpression get_operand() {
		if(this.number_of_children() < 1) {
			return null;
		}
		else {
			return (CirReferExpression) this.get_child(0);
		}
	}
	@Override
	public void set_operand(CirReferExpression operand) throws IllegalArgumentException {
		if(this.number_of_children() == 0) {
			this.add_child((CirNodeImpl) operand);
		}
		else {
			throw new IllegalArgumentException("Out of bounds: " + this.number_of_children());
		}
	}

}
