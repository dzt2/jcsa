package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class AbsDataValueState extends AbsExecutionState {

	protected AbsDataValueState(AbsExecutionStore state_store,
			SymbolExpression state_value) throws Exception {
		super(AbsExecutionClass.expr, state_store, state_value);
		if(!state_store.is_expression()) {
			throw new IllegalArgumentException("Invalid: " + state_store);
		}
	}
	
	/**
	 * @return	the original expression that the data-state describes
	 */
	public CirExpression get_expression() { 
		return (CirExpression) this.get_state_store().get_cir_location(); 
	}
	
	/**
	 * @return the value hold of the data-expression
	 */
	public SymbolExpression get_value() { return this.get_state_value(); }

	@Override
	protected AbsExecutionState copy() throws Exception {
		return new AbsDataValueState(this.get_state_store(), this.get_state_value());
	}
	
}
