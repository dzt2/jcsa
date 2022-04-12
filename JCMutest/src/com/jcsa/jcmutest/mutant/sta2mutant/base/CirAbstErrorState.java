package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public abstract class CirAbstErrorState extends CirAbstractState {

	protected CirAbstErrorState(CirAbstractClass category, CirAbstractStore location, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(category, location, loperand, roperand);
	}

}
