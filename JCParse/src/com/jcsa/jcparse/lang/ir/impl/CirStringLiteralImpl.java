package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.expr.value.CirStringLiteral;

public class CirStringLiteralImpl extends CirExpressionImpl implements CirStringLiteral {
	
	private String literal;
	
	protected CirStringLiteralImpl(CirTree tree, CType data_type, String literal) {
		super(tree, data_type);
		this.literal = literal;
	}

	@Override
	public String get_literal() { return this.literal; }

	@Override
	protected CirNode copy_self() { return new CirStringLiteralImpl(this.get_tree(), this.get_data_type(), this.literal); }

}
