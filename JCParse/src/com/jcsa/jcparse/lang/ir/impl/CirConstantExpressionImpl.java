package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.expr.value.CirConstantExpression;
import com.jcsa.jcparse.lang.lexical.CConstant;

public class CirConstantExpressionImpl extends CirExpressionImpl implements CirConstantExpression {
	
	private CConstant constant;
	
	protected CirConstantExpressionImpl(CirTree tree, CType data_type, CConstant constant) {
		super(tree, data_type);
		this.constant = constant;
	}

	@Override
	public CConstant get_constant() { return this.constant; }

	@Override
	protected CirNode copy_self() { 
		return new CirConstantExpressionImpl(this.get_tree(), this.get_data_type(), this.constant); 
	}

}
