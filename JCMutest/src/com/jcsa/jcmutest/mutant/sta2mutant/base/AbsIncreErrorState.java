package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class AbsIncreErrorState extends AbsDataErrorState {

	protected AbsIncreErrorState(AbsExecutionStore _store, SymbolExpression lvalue,
			SymbolExpression rvalue) throws Exception {
		super(AbsExecutionClass.inc_expr, _store, lvalue, rvalue);
	}
	
	/**
	 * @return the value to replace the original expression
	 */
	public SymbolExpression get_difference() { return this.get_roperand(); }

}
