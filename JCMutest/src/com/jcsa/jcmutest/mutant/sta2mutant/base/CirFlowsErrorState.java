package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;

public class CirFlowsErrorState extends CirPathErrorState {

	protected CirFlowsErrorState(CirExecutionFlow orig_flow, 
				CirExecutionFlow muta_flow) throws Exception {
		super(orig_flow.get_source(), CirStateValue.set_flow(
				orig_flow.get_target(), muta_flow.get_target()));
	}
	
	/**
	 * @return the original source statement from which the flow is mutated
	 */
	public CirExecution get_orig_source() { return this.get_execution(); }
	/**
	 * @return the next statement to be executed in the original executions
	 */
	public CirExecution get_orig_target() { return (CirExecution) this.get_ovalue().get_source(); }
	/**
	 * @return the next statement to be executed in the mutation executions
	 */
	public CirExecution get_muta_target() { return (CirExecution) this.get_mvalue().get_source(); }
	
}
