package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfEndStatement;

public class CirIfEndStatementImpl extends CirNodeImpl implements CirIfEndStatement {

	protected CirIfEndStatementImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
	}

}
