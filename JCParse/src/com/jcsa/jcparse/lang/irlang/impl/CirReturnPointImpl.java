package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirReturnPoint;

public class CirReturnPointImpl extends CirExpressionImpl implements CirReturnPoint {
	
	private static final String RETURN = "return";

	protected CirReturnPointImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
	}

	@Override
	public String get_name() { return RETURN; }
	@Override
	public String get_unique_name() {
		return RETURN + "#" + this.function_of().hashCode();
	}
	
}
