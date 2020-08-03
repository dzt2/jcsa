package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.expr.refer.CirReturnReference;

public class CirReturnReferenceImpl extends CirExpressionImpl implements CirReturnReference {
	
	private int scope_id;

	protected CirReturnReferenceImpl(CirTree tree, CType data_type, int scope_id) {
		super(tree, data_type);
		this.scope_id = scope_id;
	}

	@Override
	public String get_name(boolean complete) {
		if(complete) {
			return "return@" + this.scope_id;
		}
		else {
			return "return";
		}
	}

	@Override
	public int get_scope_id() { return this.scope_id; }

	@Override
	protected CirNode copy_self() {
		return new CirReturnReferenceImpl(this.
				get_tree(), this.get_data_type(), this.scope_id);
	}

}
