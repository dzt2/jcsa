package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.unit.CirFunctionDefinition;
import com.jcsa.jcparse.lang.ir.unit.CirTranslationUnit;

public class CirTranslationUnitImpl extends CirNodeImpl implements CirTranslationUnit {

	protected CirTranslationUnitImpl(CirTree tree) {
		super(tree);
	}

	@Override
	public int number_of_function_definitions() { return this.number_of_children(); }

	@Override
	public CirFunctionDefinition get_function_definition(int k) throws IndexOutOfBoundsException {
		return (CirFunctionDefinition) this.get_child(k);
	}

	@Override
	protected CirNode copy_self() {
		return new CirTranslationUnitImpl(this.get_tree());
	}

}
