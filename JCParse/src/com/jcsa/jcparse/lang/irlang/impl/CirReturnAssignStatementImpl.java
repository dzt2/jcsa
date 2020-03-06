package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirReturnAssignStatement;

public class CirReturnAssignStatementImpl extends CirAssignStatementImpl implements CirReturnAssignStatement {

	protected CirReturnAssignStatementImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id);
	}

}
