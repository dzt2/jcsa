package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.stmt.labeled.CirLoopBegStatement;

public class CirLoopBegStatementImpl extends CirStatementImpl implements CirLoopBegStatement {

	protected CirLoopBegStatementImpl(CirTree tree) {
		super(tree);
	}

	@Override
	protected CirNode copy_self() {
		return new CirLoopBegStatementImpl(this.get_tree());
	}

}
