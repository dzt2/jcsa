package com.jcsa.jcparse.lang.astree.impl.base;

import com.jcsa.jcparse.lang.astree.expr.oprt.AstOperator;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.lexical.COperator;

public class AstOperatorImpl extends AstFixedNode implements AstOperator {

	private COperator operator;

	public AstOperatorImpl(COperator operator) throws Exception {
		super(0);
		if (operator == null)
			throw new IllegalArgumentException("Invalid operator: null");
		else
			this.operator = operator;
	}

	@Override
	public COperator get_operator() {
		return operator;
	}

}
