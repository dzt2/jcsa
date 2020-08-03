package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.stmt.transit.CirIfEndGotoStatement;
import com.jcsa.jcparse.lang.ir.unit.CirLabel;

public class CirIfEndGotoStatementImpl extends CirStatementImpl implements CirIfEndGotoStatement {

	protected CirIfEndGotoStatementImpl(CirTree tree) {
		super(tree);
	}
	
	@Override
	public CirLabel get_next_label() { return (CirLabel) this.get_child(0); }

	@Override
	protected CirNode copy_self() {
		return new CirIfEndGotoStatementImpl(this.get_tree());
	}
	
}
