package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.expr.refer.CirTemporalReference;

public class CirTemporalReferenceImpl extends CirExpressionImpl implements CirTemporalReference {
	
	private int ast_key;
	
	protected CirTemporalReferenceImpl(CirTree tree, CType data_type, int ast_key) {
		super(tree, data_type);
		this.ast_key = ast_key;
	}

	@Override
	public String get_name(boolean complete) {
		return "@" + this.ast_key;
	}

	@Override
	public int get_ast_key() { return this.ast_key; }

	@Override
	protected CirNode copy_self() {
		return new CirTemporalReferenceImpl(this.
				get_tree(), this.get_data_type(), this.ast_key);
	}

}
