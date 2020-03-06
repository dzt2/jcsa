package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirField;
import com.jcsa.jcparse.lang.irlang.expr.CirFieldExpression;

public class CirFieldExpressionImpl extends CirExpressionImpl implements CirFieldExpression {

	protected CirFieldExpressionImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
	}

	@Override
	public CirExpression get_body() {
		if(this.number_of_children() < 1) {
			return null;
		}
		else {
			return (CirExpression) this.get_child(0);
		}
	}
	@Override
	public CirField get_field() {
		if(this.number_of_children() < 2) {
			return null;
		}
		else {
			return (CirField) this.get_child(1);
		}
	}
	
	@Override
	public void set_body(CirExpression body) throws IllegalArgumentException {
		if(this.number_of_children() == 0) {
			this.add_child((CirNodeImpl) body);
		}
		else {
			throw new IllegalArgumentException("Out of bounds: " + this.number_of_children());
		}
	}
	@Override
	public void set_field(CirField field) throws IllegalArgumentException {
		if(this.number_of_children() == 1) {
			this.add_child((CirNodeImpl) field);
		}
		else {
			throw new IllegalArgumentException("Out of bounds: " + this.number_of_children());
		}
	}

}
