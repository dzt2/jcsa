package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.expr.refer.CirIdentifierReference;

public class CirIdentifierReferenceImpl extends CirExpressionImpl implements CirIdentifierReference {

	private String name;
	private int scope_id;
	
	protected CirIdentifierReferenceImpl(CirTree tree, 
			CType data_type, String name, int scope_id) {
		super(tree, data_type);
		this.name = name;
		this.scope_id = scope_id;
	}

	@Override
	public String get_name(boolean complete) {
		if(complete) {
			return this.name + "@" + this.scope_id;
		}
		else {
			return this.name;
		}
	}

	@Override
	public String get_user_name() { return this.name; }

	@Override
	public int get_scope_id() { return this.scope_id; }

	@Override
	protected CirNode copy_self() {
		return new CirIdentifierReferenceImpl(this.get_tree(), 
				this.get_data_type(), this.name, this.scope_id);
	}

}
