package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * execution [stmt:statement] <== (set_trap:exec:expt)
 * @author yukimula
 *
 */
public class CirTrapsErrorState extends CirPathErrorState {

	protected CirTrapsErrorState(CirExecution execution) throws Exception {
		super(execution, CirStateValue.set_trap(execution));
	}

}
