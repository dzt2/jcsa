package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirCaseEndStatement;

public class CirCaseEndStatementImpl extends CirNodeImpl implements CirCaseEndStatement {

	protected CirCaseEndStatementImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
	}

}
