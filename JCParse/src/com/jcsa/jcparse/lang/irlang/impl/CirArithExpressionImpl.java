package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirArithExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class CirArithExpressionImpl extends CirExpressionImpl implements CirArithExpression {
	
	private COperator operator;
	protected CirArithExpressionImpl(CirTree tree, int node_id, COperator operator) throws IllegalArgumentException {
		super(tree, node_id, true);
		if(operator == null) {
			throw new IllegalArgumentException("invalid operator: null");
		}
		else {
			this.operator = operator;
		}
	}
	@Override
	public COperator get_operator() { return this.operator; }
	@Override
	public int number_of_operand() { return this.number_of_children(); }
	@Override
	public CirExpression get_operand(int k) throws IndexOutOfBoundsException {
		return (CirExpression) this.get_child(k);
	}
	@Override
	public void add_operand(CirExpression operand) throws IllegalArgumentException {
		this.add_child((CirNodeImpl) operand);
	}

}
