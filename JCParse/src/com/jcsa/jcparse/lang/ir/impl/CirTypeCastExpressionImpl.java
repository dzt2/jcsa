package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.expr.value.CirTypeCastExpression;
import com.jcsa.jcparse.lang.ir.unit.CirType;

public class CirTypeCastExpressionImpl extends CirExpressionImpl implements CirTypeCastExpression {

	protected CirTypeCastExpressionImpl(CirTree tree, CType data_type) {
		super(tree, data_type);
	}

	@Override
	public CirType get_cast_type() { return (CirType) this.get_child(0); }

	@Override
	public CirExpression get_operand() { return (CirExpression) this.get_child(1); }

	@Override
	protected CirNode copy_self() { return new CirTypeCastExpressionImpl(this.get_tree(), this.get_data_type()); }

}
