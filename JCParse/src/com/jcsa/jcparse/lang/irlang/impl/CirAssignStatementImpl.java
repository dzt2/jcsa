package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;

public abstract class CirAssignStatementImpl extends CirNodeImpl implements CirAssignStatement {

	protected CirAssignStatementImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
	}

	@Override
	public CirReferExpression get_lvalue() {
		if(this.number_of_children() < 1) {
			return null;
		}
		else {
			return (CirReferExpression) this.get_child(0);
		}
	}
	@Override
	public CirExpression get_rvalue() {
		if(this.number_of_children() < 2) {
			return null;
		}
		else {
			return (CirExpression) this.get_child(1);
		}
	}

	@Override
	public void set_lvalue(CirReferExpression lvalue) throws IllegalArgumentException {
		if(this.number_of_children() == 0) {
			this.add_child((CirNodeImpl) lvalue);
		}
		else {
			throw new IllegalArgumentException("Out of bounds: " + this.number_of_children());
		}
	}
	@Override
	public void set_rvalue(CirExpression rvalue) throws IllegalArgumentException {
		if(this.number_of_children() == 1) {
			this.add_child((CirNodeImpl) rvalue);
		}
		else {
			throw new IllegalArgumentException("Out of bounds: " + this.number_of_children());
		}
	}

}
