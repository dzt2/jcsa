package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirNameExpression;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionBody;
import com.jcsa.jcparse.lang.irlang.unit.CirFunctionDefinition;

public class CirFunctionDefinitionImpl extends CirNodeImpl implements CirFunctionDefinition {

	protected CirFunctionDefinitionImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
	}

	@Override
	public CirNameExpression get_declarator() {
		if(this.number_of_children() < 1) {
			return null;
		}
		else {
			return (CirNameExpression) this.get_child(0);
		}
	}
	@Override
	public CirFunctionBody get_body() {
		if(this.number_of_children() < 2) {
			return null;
		}
		else {
			return (CirFunctionBody) this.get_child(1);
		}
	}

	@Override
	public void set_declarator(CirNameExpression declarator) throws IllegalArgumentException {
		if(this.number_of_children() == 0) {
			this.add_child((CirNodeImpl) declarator);
		}
		else {
			throw new IllegalArgumentException("Out of bounds: " + this.number_of_children());
		}
	}
	@Override
	public void set_body(CirFunctionBody body) throws IllegalArgumentException {
		if(this.number_of_children() == 1) {
			this.add_child((CirNodeImpl) body);
		}
		else {
			throw new IllegalArgumentException("Out of bounds: " + this.number_of_children());
		}
	}

}
