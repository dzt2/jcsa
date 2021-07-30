package com.jcsa.jcmutest.mutant.cir2mutant;

import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * It denotes a basic infection channel with two attributes, said constraint
 * (or precondition) and initial state error (post-condition).
 * 
 * @author yukimula
 *
 */
public class CirMutation {
	
	/* definitions */
	private CirAttribute constraint;
	private CirAttribute init_error;
	public CirMutation(CirAttribute constraint, CirAttribute 
				init_error) throws IllegalArgumentException {
		if(constraint == null || !constraint.is_constraint()) {
			throw new IllegalArgumentException("Invalid constraint: " + constraint);
		}
		else if(init_error == null || !init_error.is_abst_error()) {
			throw new IllegalArgumentException("Invalid init_error: " + init_error);
		}
		else {
			this.constraint = constraint; this.init_error = init_error;
		}
	}
	
	/* getters */
	/**
	 * @return state infection condition to infect
	 */
	public CirAttribute get_constraint() { return this.constraint; }
	/**
	 * @return state error introduced in infection
	 */
	public CirAttribute get_init_error() { return this.init_error; }
	/**
	 * @return where the mutation is introduced and evaluated
	 */
	public CirExecution get_execution() { return this.init_error.get_execution(); }
	
	/* universal */
	@Override
	public String toString() { return this.constraint + " :: " + this.init_error; }
	@Override
	public int hashCode() { return this.toString().hashCode(); }
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		else if(obj instanceof CirMutation) {
			return this.toString().equals(obj.toString());
		}
		else {
			return false;
		}
	}
	
}
