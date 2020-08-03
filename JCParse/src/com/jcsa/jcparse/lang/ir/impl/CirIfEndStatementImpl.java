package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.stmt.labeled.CirIfEndStatement;

public class CirIfEndStatementImpl extends CirStatementImpl implements CirIfEndStatement {

	protected CirIfEndStatementImpl(CirTree tree) {
		super(tree);
	}

	@Override
	protected CirNode copy_self() {
		return new CirIfEndStatementImpl(this.get_tree());
	}
	
}
