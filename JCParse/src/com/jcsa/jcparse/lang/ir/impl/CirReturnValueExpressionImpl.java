package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.expr.value.CirReturnValueExpression;

public class CirReturnValueExpressionImpl extends CirExpressionImpl implements CirReturnValueExpression {

	protected CirReturnValueExpressionImpl(CirTree tree, CType data_type) {
		super(tree, data_type);
	}

	@Override
	public CirExpression get_callee() { return (CirExpression) this.get_child(0); }

	@Override
	protected CirNode copy_self() { return new CirReturnValueExpressionImpl(this.get_tree(), this.get_data_type()); }

}
