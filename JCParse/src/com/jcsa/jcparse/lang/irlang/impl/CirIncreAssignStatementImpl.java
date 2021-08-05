package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirIncreAssignStatement;

public class CirIncreAssignStatementImpl extends CirAssignStatementImpl implements CirIncreAssignStatement {

	protected CirIncreAssignStatementImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id);
	}

}
