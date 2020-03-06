package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirDefaultStatement;

public class CirDefaultStatementImpl extends CirNodeImpl implements CirDefaultStatement {

	protected CirDefaultStatementImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
	}

}
