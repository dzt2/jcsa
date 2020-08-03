package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.expr.refer.CirDeclaratorReference;
import com.jcsa.jcparse.lang.ir.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.ir.unit.CirStatementList;
import com.jcsa.jcparse.lang.ir.unit.CirType;

public class CirFunctionDefinitionImpl extends CirNodeImpl implements CirFunctionDefinition {

	protected CirFunctionDefinitionImpl(CirTree tree) {
		super(tree);
	}

	@Override
	public CirType get_specifiers() { return (CirType) this.get_child(0); }

	@Override
	public CirDeclaratorReference get_declarator() { 
		return (CirDeclaratorReference) this.get_child(1); 
	}

	@Override
	public CirStatementList get_body() { return (CirStatementList) this.get_child(2); }

	@Override
	protected CirNode copy_self() { return new CirFunctionDefinitionImpl(this.get_tree()); }

}
