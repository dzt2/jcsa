package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirWaitAssignStatement;

public class CirWaitAssignStatementImpl extends CirAssignStatementImpl implements CirWaitAssignStatement {

	protected CirWaitAssignStatementImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id);
	}

}
