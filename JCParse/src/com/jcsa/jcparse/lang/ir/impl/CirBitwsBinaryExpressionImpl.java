package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.expr.value.CirBitwsBinaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class CirBitwsBinaryExpressionImpl extends CirExpressionImpl implements CirBitwsBinaryExpression {
	
	private COperator operator;
	
	protected CirBitwsBinaryExpressionImpl(CirTree tree, CType data_type, COperator operator) {
		super(tree, data_type);
		this.operator = operator;
	}

	@Override
	public COperator get_operator() { return this.operator; }

	@Override
	public CirExpression get_loperand() { return (CirExpression) this.get_child(0); }

	@Override
	public CirExpression get_roperand() { return (CirExpression) this.get_child(1); }

	@Override
	protected CirNode copy_self() {
		return new CirBitwsBinaryExpressionImpl(this.get_tree(), this.get_data_type(), this.operator);
	}
	
}
