package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.stmt.labeled.CirSwitchEndStatement;

public class CirSwitchEndStatementImpl extends CirStatementImpl implements CirSwitchEndStatement {

	protected CirSwitchEndStatementImpl(CirTree tree) {
		super(tree);
	}

	@Override
	protected CirNode copy_self() {
		return new CirSwitchEndStatementImpl(this.get_tree());
	}

}
