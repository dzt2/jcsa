package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirBinAssignStatement;

public class CirBinAssignStatementImpl extends CirAssignStatementImpl implements CirBinAssignStatement {

	protected CirBinAssignStatementImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id);
	}

}
