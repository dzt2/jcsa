package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;

/**
 * [stmt:statement] <== set_flow(orig_target, muta_target)
 * @author yukimula
 *
 */
public class CirFlowsErrorState extends CirPathErrorState {

	protected CirFlowsErrorState(CirExecutionFlow orig_flow, 
			CirExecutionFlow muta_flow) throws Exception {
		super(orig_flow.get_source(), CirStateValue.set_flow(
				orig_flow.get_target(), muta_flow.get_target()));
	}
	
	/**
	 * @return the next statement being executed in original version
	 */
	public CirExecution get_orig_target() { 
		return (CirExecution) this.get_loperand().get_source(); 
	}
	/**
	 * @return the next statement being executed in mutation version
	 */
	public CirExecution get_muta_target() { 
		return (CirExecution) this.get_roperand().get_source(); 
	}

}
