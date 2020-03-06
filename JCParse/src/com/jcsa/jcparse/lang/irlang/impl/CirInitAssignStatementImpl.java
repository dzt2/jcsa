package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirInitAssignStatement;

public class CirInitAssignStatementImpl extends CirAssignStatementImpl implements CirInitAssignStatement {

	protected CirInitAssignStatementImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id);
	}

}
