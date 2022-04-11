package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class UniValueErrorState extends UniDataErrorState {

	protected UniValueErrorState(CirNode location, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(UniAbstractClass.set_expr, location, loperand, roperand);
	}
	
	/**
	 * @return the value to replace the original ones.
	 */
	public SymbolExpression get_mutated_value() { return this.get_roperand(); }

}
