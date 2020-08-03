package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.expr.value.CirLogicUnaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class CirLogicUnaryExpressionImpl extends CirExpressionImpl implements CirLogicUnaryExpression {

	protected CirLogicUnaryExpressionImpl(CirTree tree, CType data_type) {
		super(tree, data_type);
	}
	
	@Override
	public COperator get_operator() { return COperator.logic_not; }

	@Override
	public CirExpression get_operand() { return (CirExpression) this.get_child(0); }

	@Override
	protected CirNode copy_self() { return new CirLogicUnaryExpressionImpl(this.get_tree(), this.get_data_type()); }

}
