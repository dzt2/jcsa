package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.stmt.CirStatement;
import com.jcsa.jcparse.lang.ir.unit.CirStatementList;

public abstract class CirStatementImpl extends CirNodeImpl implements CirStatement {

	protected CirStatementImpl(CirTree tree) {
		super(tree);
	}

	@Override
	public CirStatementList get_function_body() {
		return (CirStatementList) this.get_parent();
	}

	@Override
	public int get_statement_label() {
		return this.get_child_index();
	}

}
