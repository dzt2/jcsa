package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;

public class CirCaseStatementImpl extends CirNodeImpl implements CirCaseStatement {

	protected CirCaseStatementImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
	}

	@Override
	public CirExpression get_condition() {
		if(this.number_of_children() < 1) {
			return null;
		}
		else {
			return (CirExpression) this.get_child(0);
		}
	}
	@Override
	public CirLabel get_false_label() {
		if(this.number_of_children() < 2) {
			return null;
		}
		else {
			return (CirLabel) this.get_child(1);
		}
	}

	@Override
	public void set_condition(CirExpression condition) throws IllegalArgumentException {
		if(this.number_of_children() == 0) {
			this.add_child((CirNodeImpl) condition);
		}
		else {
			throw new IllegalArgumentException("Out of bounds: " + this.number_of_children());
		}
	}
	@Override
	public void set_false_branch(CirLabel label) throws IllegalArgumentException {
		if(this.number_of_children() == 1) {
			this.add_child((CirNodeImpl) label);
		}
		else {
			throw new IllegalArgumentException("Out of bounds: " + this.number_of_children());
		}
	}
	
}
