package com.jcsa.jcmutest.mutant.cir2mutant;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConditionState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirSyMutationState;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * 	It represents a mutation specified in the level of C-intermediate representative
 * 	program code (rather than source code before parsed).<br>
 * 	
 * 	A <code>CirMutation</code> specifies three elements to describe the semantics
 * 	of each mutation's killability, said:<br>
 * 
 * 	-- a_mutation: 	the syntactic mutation as the context-insensitive mutant form;	<br>
 * 	-- constraint:	the state infection condition evaluated at mutated statement;	<br>
 * 	-- init_error:	the initial error introduced by the mutant in execution state;	<br>
 * 	
 * 	@author yukimula
 *
 */
public class CirMutation {
	
	private CirSyMutationState 	r_state;
	private CirConditionState	i_state;
	private CirAbstErrorState	p_state;
	protected CirMutation(Mutant mutant, CirExecution execution,
			CirConditionState i_state, CirAbstErrorState p_state) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else if(i_state == null) {
			throw new IllegalArgumentException("Invalid i_state: null");
		}
		else if(p_state == null || !p_state.is_abst_error()) {
			throw new IllegalArgumentException("Invalid p_state: null");
		}
		else {
			this.r_state = CirAbstractState.ast_muta(execution, mutant.get_id(),
					mutant.get_mutation().get_operator().toString());
			this.i_state = i_state;
			this.p_state = p_state;
		}
	}
	
	/* getters */
	/**
	 * @return the execution point where the state is preserved
	 */
	public CirExecution			get_execution() { return this.r_state.get_execution(); }
	/**
	 * @return the reachability state
	 */
	public CirSyMutationState	get_r_state() { return this.r_state; }
	/**
	 * @return the infection state
	 */
	public CirConditionState	get_i_state() { return this.i_state; }
	/**
	 * @return the propagation state
	 */
	public CirAbstErrorState	get_p_state() { return this.p_state; }
	
}
