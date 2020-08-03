package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.stmt.transit.CirLoopContinueStatement;
import com.jcsa.jcparse.lang.ir.unit.CirLabel;

public class CirLoopContinueStatementImpl extends CirStatementImpl implements CirLoopContinueStatement {

	protected CirLoopContinueStatementImpl(CirTree tree) {
		super(tree);
	}
	
	@Override
	public CirLabel get_next_label() { return (CirLabel) this.get_child(0); }

	@Override
	protected CirNode copy_self() {
		return new CirLoopContinueStatementImpl(this.get_tree());
	}
	
}
