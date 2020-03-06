package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirDefaultValue;

public class CirDefaultValueImpl extends CirExpressionImpl implements CirDefaultValue {

	protected CirDefaultValueImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
	}

}
