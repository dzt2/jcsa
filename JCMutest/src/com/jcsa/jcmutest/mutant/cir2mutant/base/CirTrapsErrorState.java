package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

public class CirTrapsErrorState extends CirPathErrorState {

	protected CirTrapsErrorState(CirExecution execution) throws Exception {
		super(CirAbstractClass.trp_stmt, execution, 
				CirMutations.trap_value, 
				CirMutations.trap_value);
	}

}
