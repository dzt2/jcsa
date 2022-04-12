package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class CirValueErrorState extends CirDataErrorState {

	protected CirValueErrorState(CirAbstractStore location, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(CirAbstractClass.set_expr, location, loperand, roperand);
	}
	
	/**
	 * @return the value to replace the original one in this state
	 */
	public SymbolExpression get_mutation_value() { return this.get_roperand(); }

}
