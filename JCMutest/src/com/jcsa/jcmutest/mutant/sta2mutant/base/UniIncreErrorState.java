package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class UniIncreErrorState extends UniDataErrorState {

	protected UniIncreErrorState(CirNode location, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(UniAbstractClass.inc_expr, location, loperand, roperand);
	}
	
	public SymbolExpression get_difference() { return this.get_roperand(); }
	
}
