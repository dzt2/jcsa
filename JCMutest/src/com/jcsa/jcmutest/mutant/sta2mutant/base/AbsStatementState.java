package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;
import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class AbsStatementState extends AbsExecutionState {

	protected AbsStatementState(AbsExecutionStore state_store,
			SymbolExpression state_value) throws Exception {
		super(AbsExecutionClass.exec, state_store, state_value);
		if(!state_store.is_statement()) {
			throw new IllegalArgumentException("Invalid: " + state_store);
		}
	}
	
	/**
	 * @return whether the statement should be executed
	 */
	public boolean is_executed() {
		SymbolExpression value = this.get_state_value();
		if(value instanceof SymbolConstant) {
			return ((SymbolConstant) value).get_bool();
		}
		else {
			return false;
		}
	}
	
	/**
	 * @return whether the statement should not be executed
	 */
	public boolean is_not_executed() {
		SymbolExpression value = this.get_state_value();
		if(value instanceof SymbolConstant) {
			return !((SymbolConstant) value).get_bool();
		}
		else {
			return false;
		}
	}
	
	/**
	 * @return whether the state represents a trapping at this point
	 */
	public boolean is_trap_over() {
		return StateMutations.has_trap_value(this.get_state_value());
	}

	
	@Override
	protected AbsExecutionState copy() throws Exception {
		return new AbsStatementState(this.get_state_store(), this.get_state_value());
	}
	
}
