package com.jcsa.jcmutest.mutant.sta2mutant;

import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.sta2mutant.base.CirConditionState;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * It represents the abstract execution states correlated with a syntactic 
 * mutation directly, including:<br>
 * <br>
 * 	(1)	r_state:	cov_stmt(execution, statement, 1)						<br>
 * 	(2)	i_state:	eva_expr|cov_stmt(execution, statement, [condition]);	<br>
 * 	(3)	e_state:	mut_xxx(execution, expression|statement, [mut_value]);	<br>
 * <br>
 * @author yukimula
 *
 */
public class StateMutation {
	
	/* definitions */
	private CirConditionState	rstate;
	private CirConditionState 	istate;
	private	CirAbstErrorState	pstate;
	protected StateMutation(CirExecution execution,
			CirConditionState istate,
			CirAbstErrorState pstate) throws Exception {
		if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(istate == null) {
			throw new IllegalArgumentException("Invalid istate as: null");
		}
		else if(pstate == null) {
			throw new IllegalArgumentException("Invalid pstate as: null");
		}
		else {
			this.rstate = CirAbstractState.cov_time(execution, 1);
			this.istate = istate;
			this.pstate = pstate;
		}
	}
	
	/* getters */
	/**
	 * @return the execution point where the mutation is injected
	 */
	public CirExecution get_r_execution() { return this.rstate.get_execution(); }
	/**
	 * @return the reachability state
	 */ 
	public CirConditionState get_rstate() { return this.rstate; }
	/**
	 * @return the infection state
	 */
	public CirConditionState get_istate() { return this.istate; }
	/**
	 * @return the propagation state of initial error
	 */
	public CirAbstErrorState get_pstate() { return this.pstate; }
	
}
