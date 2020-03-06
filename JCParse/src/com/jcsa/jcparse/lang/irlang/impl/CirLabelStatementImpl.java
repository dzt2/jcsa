package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabelStatement;

public class CirLabelStatementImpl extends CirNodeImpl implements CirLabelStatement {

	protected CirLabelStatementImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
	}

}
