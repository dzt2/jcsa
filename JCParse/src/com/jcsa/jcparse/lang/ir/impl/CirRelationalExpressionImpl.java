package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.expr.value.CirRelationalExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class CirRelationalExpressionImpl extends CirExpressionImpl implements CirRelationalExpression {

	private COperator operator;
	
	protected CirRelationalExpressionImpl(CirTree tree, CType data_type, COperator operator) {
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
		return new CirRelationalExpressionImpl(this.get_tree(), this.get_data_type(), this.operator);
	}

}
