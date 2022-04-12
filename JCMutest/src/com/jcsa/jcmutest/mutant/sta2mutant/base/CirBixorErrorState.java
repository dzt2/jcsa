package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class CirBixorErrorState extends CirDataErrorState {

	protected CirBixorErrorState(CirAbstractStore location, 
			SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(CirAbstractClass.xor_expr, location, loperand, roperand);
	}
	
	/**
	 * @return the value to increment on the original one in this state
	 */
	public SymbolExpression get_difference() { return this.get_roperand(); }
	
}
