package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.expr.refer.CirFieldExpression;
import com.jcsa.jcparse.lang.ir.expr.refer.CirReferExpression;
import com.jcsa.jcparse.lang.ir.unit.CirField;

public class CirFieldExpressionImpl extends CirExpressionImpl implements CirFieldExpression {

	protected CirFieldExpressionImpl(CirTree tree, CType data_type) {
		super(tree, data_type);
	}

	@Override
	public CirReferExpression get_body() { return (CirReferExpression) this.get_child(0); }

	@Override
	public CirField get_field() { return (CirField) this.get_child(1); }

	@Override
	protected CirNode copy_self() { return new CirFieldExpressionImpl(this.get_tree(), this.get_data_type()); }

}
