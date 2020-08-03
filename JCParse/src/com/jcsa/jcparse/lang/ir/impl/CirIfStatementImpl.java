package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.stmt.transit.CirIfStatement;
import com.jcsa.jcparse.lang.ir.unit.CirLabel;

public class CirIfStatementImpl extends CirStatementImpl implements CirIfStatement {

	protected CirIfStatementImpl(CirTree tree) {
		super(tree);
	}

	@Override
	public CirExpression get_condition() { return (CirExpression) this.get_child(0); }

	@Override
	public CirLabel get_true_label() { return (CirLabel) this.get_child(1); }

	@Override
	public CirLabel get_false_label() { return (CirLabel) this.get_child(2); }

	@Override
	protected CirNode copy_self() {
		return new CirIfStatementImpl(this.get_tree());
	}

}
