package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public abstract class CirExpressionImpl extends CirNodeImpl implements CirExpression {

	protected CirExpressionImpl(CirTree tree, int node_id, boolean linked) throws IllegalArgumentException {
		super(tree, node_id, linked);
	}

	private CType data_type;
	@Override
	public CType get_data_type() { return this.data_type; }
	@Override
	public void set_data_type(CType type) {
		this.data_type = type;
	}

	@Override
	public CirStatement statement_of() {
		CirNode node = this;
		while(node != null) {
			if(node instanceof CirStatement)
				break;
			else node = node.get_parent();
		}
		return (CirStatement) node;
	}
}
