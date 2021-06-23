package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * It connects each mutant to the pair of infection-constraint and initial-state-error.
 * 
 * @author yukimula
 *
 */
public class CirMutation {
	
	/* attributes */
	/** state infection constraint **/
	private SymCondition constraint;
	/** initial errors be infected **/
	private SymCondition init_error;
	/**
	 * create an infection-error pair as directly connection of the mutant
	 * @param constraint
	 * @param init_error
	 * @throws Exception
	 */
	protected CirMutation(SymCondition constraint, SymCondition init_error) throws Exception {
		if(constraint == null || !constraint.is_constraint()) {
			throw new IllegalArgumentException("Invalid constriant: null");
		}
		else if(init_error == null || !init_error.is_state_error()) {
			throw new IllegalArgumentException("Invalid init_error: null");
		}
		else {
			this.constraint = constraint; this.init_error = init_error;
		}
	}
	
	/* getters */
	/**
	 * @return where the infection should occur for killing a mutant
	 */
	public CirExecution get_execution() { return this.init_error.get_execution(); }
	/**
	 * @return the constraint that needs to be satisfied for killing mutation
	 */
	public SymCondition get_constraint() { return this.constraint; }
	/**
	 * @return the state error that is expected to occur for killing mutation
	 */
	public SymCondition get_init_error() { return this.init_error; } 
	
	/* comparator */
	@Override
	public String toString() { 
		return this.constraint.toString() + " ==> " + this.init_error.toString();
	}
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		else if(obj instanceof CirMutation)
			return obj.toString().equals(this.toString());
		else
			return false;
	}
	
}
