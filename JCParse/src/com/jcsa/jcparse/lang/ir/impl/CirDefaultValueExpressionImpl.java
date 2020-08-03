package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.expr.value.CirDefaultValueExpression;

public class CirDefaultValueExpressionImpl extends CirExpressionImpl implements CirDefaultValueExpression {

	protected CirDefaultValueExpressionImpl(CirTree tree, CType data_type) {
		super(tree, data_type);
	}

	@Override
	protected CirNode copy_self() { return new CirDefaultValueExpressionImpl(this.get_tree(), this.get_data_type()); }

}
