package com.jcsa.jcmutest.mutant.uni2mutant.base.impl;

import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractClass;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractStore;

/**
 * 	trp_stmt(statement; exception, exception)
 * 	@author yukimula
 *
 */
public class UniTrapsErrorState extends UniPathErrorState {

	protected UniTrapsErrorState(UniAbstractStore state_store) throws Exception {
		super(UniAbstractClass.trp_stmt, state_store, UniAbstractStates.trap_value, UniAbstractStates.trap_value);
	}

}
