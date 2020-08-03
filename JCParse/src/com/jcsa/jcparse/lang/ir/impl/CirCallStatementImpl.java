package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.stmt.transit.CirCallStatement;
import com.jcsa.jcparse.lang.ir.unit.CirArgumentList;

public class CirCallStatementImpl extends CirStatementImpl implements CirCallStatement {

	protected CirCallStatementImpl(CirTree tree) {
		super(tree);
	}

	@Override
	public CirExpression get_callee() { return (CirExpression) this.get_child(0); }

	@Override
	public CirArgumentList get_arguments() { return (CirArgumentList) this.get_child(1); }

	@Override
	protected CirNode copy_self() {
		return new CirCallStatementImpl(this.get_tree());
	}

}
