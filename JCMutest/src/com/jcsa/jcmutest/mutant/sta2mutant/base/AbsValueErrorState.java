package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class AbsValueErrorState extends AbsDataErrorState {

	protected AbsValueErrorState(AbsExecutionStore _store, SymbolExpression lvalue,
			SymbolExpression rvalue) throws Exception {
		super(AbsExecutionClass.set_expr, _store, lvalue, rvalue);
	}
	
	/**
	 * @return the value to replace the original expression
	 */
	public SymbolExpression get_mutated_value() { return this.get_roperand(); }
	
}
