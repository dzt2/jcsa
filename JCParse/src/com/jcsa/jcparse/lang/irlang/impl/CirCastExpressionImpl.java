package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirCastExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirType;

public class CirCastExpressionImpl extends CirExpressionImpl implements CirCastExpression {

	protected CirCastExpressionImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
	}

	@Override
	public CirType get_type() {
		if(this.number_of_children() < 1) {
			return null;
		}
		else {
			return (CirType) this.get_child(0);
		}
	}
	@Override
	public CirExpression get_operand() {
		if(this.number_of_children() < 2) {
			return null;
		}
		else {
			return (CirExpression) this.get_child(1);
		}
	}

	@Override
	public void set_type(CirType type) throws IllegalArgumentException {
		if(this.number_of_children() == 0) {
			this.add_child((CirNodeImpl) type);
		}
		else {
			throw new IllegalArgumentException("Out of bounds: " + this.number_of_children());
		}
	}
	@Override
	public void set_operand(CirExpression operand) throws IllegalArgumentException {
		if(this.number_of_children() == 1) {
			this.add_child((CirNodeImpl) operand);
		}
		else {
			throw new IllegalArgumentException("Out of bounds: " + this.number_of_children());
		}
	}

}
