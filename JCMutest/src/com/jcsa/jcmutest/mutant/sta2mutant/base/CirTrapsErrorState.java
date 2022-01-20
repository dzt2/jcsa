package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * [stmt:statement] <== set_trap(execution, exception)
 * @author yukimula
 *
 */
public class CirTrapsErrorState extends CirPathErrorState {

	protected CirTrapsErrorState(CirExecution point) throws Exception {
		super(point, CirStateValue.set_trap(point));
	}

}
