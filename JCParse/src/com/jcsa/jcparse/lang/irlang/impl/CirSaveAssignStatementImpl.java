package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirSaveAssignStatement;

public class CirSaveAssignStatementImpl extends CirAssignStatementImpl implements CirSaveAssignStatement {

	protected CirSaveAssignStatementImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id);
	}

}
