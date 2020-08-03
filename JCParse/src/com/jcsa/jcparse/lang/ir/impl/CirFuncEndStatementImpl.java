package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.stmt.labeled.CirFuncEndStatement;

public class CirFuncEndStatementImpl extends CirStatementImpl implements CirFuncEndStatement {

	protected CirFuncEndStatementImpl(CirTree tree) {
		super(tree);
	}

	@Override
	protected CirNode copy_self() {
		return new CirFuncEndStatementImpl(this.get_tree());
	}

}
