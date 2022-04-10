package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public abstract class AbsDataErrorState extends AbsExecutionState {

	protected AbsDataErrorState(AbsExecutionClass _class, 
			AbsExecutionStore _store, SymbolExpression lvalue,
			SymbolExpression rvalue) throws Exception {
		super(_class, _store, lvalue, rvalue);
		if(!_store.is_expression()) {
			throw new IllegalArgumentException("Invalid: " + _store);
		}
	}
	
	/**
	 * @return the expression to be replaced in the data-error state
	 */
	public CirExpression get_expression() { return (CirExpression) this.get_state_store().get_cir_location(); }
	
	/**
	 * @return the original value hold in the data-error state
	 */
	public SymbolExpression get_original_value() { return this.get_loperand(); }
	
}
