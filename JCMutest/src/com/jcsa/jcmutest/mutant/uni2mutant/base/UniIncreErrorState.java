package com.jcsa.jcmutest.mutant.uni2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class UniIncreErrorState extends UniDataErrorState {

	protected UniIncreErrorState(UniAbstractStore _store, SymbolExpression lvalue,
			SymbolExpression rvalue) throws Exception {
		super(UniAbstractClass.inc_expr, _store, lvalue, rvalue);
	}
	
	/**
	 * @return the difference from the original value of expression
	 */
	public SymbolExpression get_difference() { return this.get_roperand(); }

}
