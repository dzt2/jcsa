package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public abstract class AbsPathErrorState extends AbsExecutionState {

	protected AbsPathErrorState(AbsExecutionClass _class, 
			AbsExecutionStore _store, SymbolExpression lvalue,
			SymbolExpression rvalue) throws Exception {
		super(_class, _store, lvalue, rvalue);
		if(!_store.is_statement()) {
			throw new IllegalArgumentException("Invalid store: " + _store);
		}
	}
	
	/**
	 * @return the statement in which the condition is evaluated
	 */
	public CirStatement get_statement() { 
		return (CirStatement) this.get_state_store().get_cir_location(); 
	}
	
}
