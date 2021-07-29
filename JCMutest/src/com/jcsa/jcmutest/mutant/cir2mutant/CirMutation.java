package com.jcsa.jcmutest.mutant.cir2mutant;

import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;

/**
 * 	It represents a state infection channel with a constraint (precondition) and 
 * 	initial state error (post-condition) around the mutated code location. <br>
 * 	
 * 	@author yukimula
 */
public class CirMutation {
	
	/* attributes */
	private CirAttribute constraint;
	private CirAttribute init_error;
	
	/* constructor */
	/**
	 * @param constraint the precondition to cause infection in CIR source code
	 * @param init_error the initial state error being caused, after infections
	 * @throws IllegalArgumentException
	 */
	public CirMutation(CirAttribute constraint, CirAttribute init_error) throws IllegalArgumentException {
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
	 * @return the precondition to cause infection in CIR source code
	 */
	public CirAttribute get_constraint() { return this.constraint; }
	/**
	 * @return the initial state error being caused, after infections
	 */
	public CirAttribute get_init_error() { return this.init_error; }
	
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
