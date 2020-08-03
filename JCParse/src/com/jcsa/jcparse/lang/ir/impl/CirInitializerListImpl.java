package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.expr.value.CirInitializerList;

public class CirInitializerListImpl extends CirExpressionImpl implements CirInitializerList {

	protected CirInitializerListImpl(CirTree tree, CType data_type) {
		super(tree, data_type);
	}

	@Override
	public int number_of_elements() { return this.number_of_children(); }

	@Override
	public CirExpression get_element(int k) throws IndexOutOfBoundsException {
		return (CirExpression) this.get_child(k);
	}

	@Override
	protected CirNode copy_self() {
		return new CirInitializerListImpl(this.get_tree(), this.get_data_type());
	}

}
