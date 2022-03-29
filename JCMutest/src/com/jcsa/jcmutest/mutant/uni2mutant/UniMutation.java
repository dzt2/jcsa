package com.jcsa.jcmutest.mutant.uni2mutant;

import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstErrorState;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractStates;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractStore;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniConditionState;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * 	It models the mutation as RIP form to model the mutation.
 * 	@author yukimula
 *
 */
public class UniMutation {
	
	/* definitions */
	/** the state to require the coverage of faulty statement **/
	private	UniConditionState	r_state;
	/** the state to evaluate the state infection constraints **/
	private	UniConditionState	i_state;
	/** the state to represent the initial error state **/
	private	UniAbstErrorState	p_state;
	/**
	 * @param statement	the faulty statement where the mutant is seeded
	 * @param i_state	the state to evaluate state infection condition
	 * @param p_state	the state to represent the initial error states
	 * @throws Exception
	 */
	public	UniMutation(CirStatement statement, 
			UniConditionState i_state,
			UniAbstErrorState p_state) throws Exception {
		if(statement == null) {
			throw new IllegalArgumentException("Invalid statement: null");
		}
		else if(i_state == null) {
			throw new IllegalArgumentException("Invalid i_state as null");
		}
		else if(p_state == null) {
			throw new IllegalArgumentException("Invalid p_state as null");
		}
		else {
			this.r_state = UniAbstractStates.cov_time(UniAbstractStore.new_node(statement), 1, 1);
			this.i_state = i_state;
			this.p_state = p_state;
		}
	}
	
	/* getters */
	/**
	 * @return	the statement where this mutation is injected
	 */
	public CirExecution			get_execution() { return this.r_state.get_execution(); }
	/**
	 * @return	the state to require the coverage of the mutant
	 */
	public UniConditionState	get_r_state() { return this.r_state; }
	/**
	 * @return	the state to evaluate state infection condition
	 */
	public UniConditionState	get_i_state() { return this.i_state; }
	/**
	 * @return	the state to represent the initial error states
	 */
	public UniAbstErrorState	get_p_state() { return this.p_state; }
	
}
