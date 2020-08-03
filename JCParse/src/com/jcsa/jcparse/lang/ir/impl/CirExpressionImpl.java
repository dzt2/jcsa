package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.stmt.CirStatement;

public abstract class CirExpressionImpl extends CirNodeImpl implements CirExpression {
	
	private CType data_type;
	
	protected CirExpressionImpl(CirTree tree, CType data_type) {
		super(tree);
		this.data_type = data_type;
	}

	@Override
	public CType get_data_type() { return this.data_type; }

	@Override
	public CirStatement get_statement() {
		CirNode node = this;
		while(node != null) {
			if(node instanceof CirStatement) {
				return (CirStatement) node;
			}
			else {
				node = node.get_parent();
			}
		}
		return null;
	}

}
