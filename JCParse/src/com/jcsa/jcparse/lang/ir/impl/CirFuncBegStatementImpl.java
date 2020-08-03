package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.stmt.labeled.CirFuncBegStatement;

public class CirFuncBegStatementImpl extends CirStatementImpl implements CirFuncBegStatement {

	protected CirFuncBegStatementImpl(CirTree tree) {
		super(tree);
	}

	@Override
	protected CirNode copy_self() {
		return new CirFuncBegStatementImpl(this.get_tree());
	}

}
