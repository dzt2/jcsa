package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirConstExpression;
import com.jcsa.jcparse.lang.lexical.CConstant;

public class CirConstExpressionImpl extends CirExpressionImpl implements CirConstExpression {

	protected CirConstExpressionImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
	}

	private CConstant constant;
	@Override
	public CConstant get_constant() { return this.constant; }
	@Override
	public void set_constant(CConstant constant) throws IllegalArgumentException {
		if(constant == null) {
			throw new IllegalArgumentException("Invalid constant: null");
		}
		else {
			this.constant = constant;
		}
	}

}
