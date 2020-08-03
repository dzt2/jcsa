package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.unit.CirType;

public class CirTypeImpl extends CirNodeImpl implements CirType {
	
	private CType data_type;

	protected CirTypeImpl(CirTree tree, CType data_type) {
		super(tree);
		this.data_type = data_type;
	}

	@Override
	public CType get_data_type() { return this.data_type; }

	@Override
	protected CirNode copy_self() {
		return new CirTypeImpl(this.get_tree(), this.data_type);
	}

}
