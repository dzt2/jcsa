package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirArgumentList;
import com.jcsa.jcparse.lang.irlang.stmt.CirCallStatement;

public class CirCallStatementImpl extends CirNodeImpl implements CirCallStatement {

	protected CirCallStatementImpl(CirTree tree, int node_id) throws IllegalArgumentException {
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
	public CirArgumentList get_arguments() {
		if(this.number_of_children() < 2) {
			return null;
		}
		else {
			return (CirArgumentList) this.get_child(1);
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
	@Override
	public void set_arguments(CirArgumentList arguments) throws IllegalArgumentException {
		if(this.number_of_children() == 1) {
			this.add_child((CirNodeImpl) arguments);
		}
		else {
			throw new IllegalArgumentException("Out of bounds: " + this.number_of_children());
		}
	}

}
