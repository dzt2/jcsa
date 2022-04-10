package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class AbsBixorErrorState extends AbsDataErrorState {

	protected AbsBixorErrorState(AbsExecutionStore _store, SymbolExpression lvalue,
			SymbolExpression rvalue) throws Exception {
		super(AbsExecutionClass.xor_expr, _store, lvalue, rvalue);
	}
	
	/**
	 * @return the value to replace the original expression
	 */
	public SymbolExpression get_difference() { return this.get_roperand(); }

}
