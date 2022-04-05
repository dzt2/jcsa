package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class AbsConstrainState extends AbsExecutionState {

	protected AbsConstrainState(AbsExecutionStore state_store,
			SymbolExpression state_value) throws Exception {
		super(AbsExecutionClass.eval, state_store, state_value);
		if(!state_store.is_statement()) {
			throw new IllegalArgumentException("Invalid: " + state_store);
		}
	}

	@Override
	protected AbsExecutionState copy() throws Exception {
		return new AbsConstrainState(this.get_state_store(), this.get_state_value());
	}
	
	/**
	 * @return the symbolic condition being evaluated at this point
	 */
	public SymbolExpression get_condition() { return this.get_state_value(); }

}
