package com.jcsa.jcmutest.mutant.uni2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public abstract class UniAbstErrorState extends UniAbstractState {

	protected UniAbstErrorState(UniAbstractClass _class, UniAbstractStore _store, SymbolExpression lvalue,
			SymbolExpression rvalue) throws Exception {
		super(_class, _store, lvalue, rvalue);
	}

}
