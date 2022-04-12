package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class CirIncreErrorState extends CirDataErrorState {

	protected CirIncreErrorState(CirAbstractStore location, 
			SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(CirAbstractClass.inc_expr, location, loperand, roperand);
	}
	
	/**
	 * @return the value to increment on the original one in this state
	 */
	public SymbolExpression get_difference() { return this.get_roperand(); }
	
}
