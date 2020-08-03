package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.stmt.CirStatement;
import com.jcsa.jcparse.lang.ir.unit.CirStatementList;

public class CirStatementListImpl extends CirNodeImpl implements CirStatementList {

	protected CirStatementListImpl(CirTree tree) {
		super(tree);
	}

	@Override
	public int number_of_statements() { return this.number_of_children(); }

	@Override
	public CirStatement get_statement(int k) throws IndexOutOfBoundsException {
		return (CirStatement) this.get_child(k);
	}

	@Override
	protected CirNode copy_self() {
		return new CirStatementListImpl(this.get_tree());
	}

}
