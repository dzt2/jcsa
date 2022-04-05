package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolConstant;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class AbsCoverTimeState extends AbsExecutionState {

	protected AbsCoverTimeState(AbsExecutionStore 
			state_store, int min_times) throws Exception {
		super(AbsExecutionClass.covt, state_store, 
				SymbolFactory.sym_constant(Integer.valueOf(min_times)));
		if(!state_store.is_statement()) {
			throw new IllegalArgumentException("Invalid: " + state_store);
		}
	}
	
	/**
	 * @return the minimal times for running the target statement
	 */
	public int get_minimal_times() { return ((SymbolConstant) this.get_state_value()).get_int(); }

	@Override
	protected AbsExecutionState copy() throws Exception {
		return new AbsCoverTimeState(this.get_state_store(), this.get_minimal_times());
	}
	
}
