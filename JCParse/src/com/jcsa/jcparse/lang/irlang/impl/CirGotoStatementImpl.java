package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirGotoStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;

public class CirGotoStatementImpl extends CirNodeImpl implements CirGotoStatement {

	protected CirGotoStatementImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
	}

	@Override
	public CirLabel get_label() {
		if(this.number_of_children() == 1) {
			return (CirLabel) this.get_child(0);
		}
		else {
			return null;
		}
	}
	@Override
	public void set_label(CirLabel label) throws IllegalArgumentException {
		if(this.number_of_children() == 0) {
			this.add_child((CirNodeImpl) label);
		}
		else {
			throw new IllegalArgumentException("invalid: null");
		}
	}

}
