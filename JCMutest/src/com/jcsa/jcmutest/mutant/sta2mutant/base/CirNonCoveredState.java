package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * It requires the statement should NOT be executed.
 * 
 * @author yukimula
 *
 */
public class CirNonCoveredState extends CirConditionState {

	protected CirNonCoveredState(CirExecution execution) throws Exception {
		super(execution, CirStateValue.non_stmt(execution));
	}
	
}
