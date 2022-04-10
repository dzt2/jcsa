package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcmutest.mutant.sta2mutant.StateMutations;

public class AbsTrapsErrorState extends AbsPathErrorState {

	protected AbsTrapsErrorState(AbsExecutionStore _store) throws Exception {
		super(AbsExecutionClass.trp_stmt, _store, 
				StateMutations.trap_value, StateMutations.trap_value);
	}

}
