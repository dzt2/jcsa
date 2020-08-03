package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.expr.value.CirBitwsUnaryExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class CirBitwsUnaryExpressionImpl extends CirExpressionImpl implements CirBitwsUnaryExpression {

	protected CirBitwsUnaryExpressionImpl(CirTree tree, CType data_type) {
		super(tree, data_type);
	}

	@Override
	public COperator get_operator() { return COperator.bit_not; }

	@Override
	public CirExpression get_operand() { return (CirExpression) this.get_child(0); }

	@Override
	protected CirNode copy_self() { return new CirBitwsUnaryExpressionImpl(this.get_tree(), this.get_data_type()); }

}
