package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.stmt.labeled.CirLoopEndStatement;

public class CirLoopEndStatementImpl extends CirStatementImpl implements CirLoopEndStatement {

	protected CirLoopEndStatementImpl(CirTree tree) {
		super(tree);
	}

	@Override
	protected CirNode copy_self() {
		return new CirLoopEndStatementImpl(this.get_tree());
	}

}
