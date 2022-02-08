package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public abstract class CirAbstErrorState extends CirAbstractState {

	protected CirAbstErrorState(CirAbstractClass category, CirExecution execution, CirNode location,
			SymbolExpression identifier, SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		super(category, execution, location, identifier, loperand, roperand);
	}

}
