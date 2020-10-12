package com.jcsa.jcmutest.mutant.cir2mutant.model;

import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;


/**
 * The mutation in C-intermediate representation code defines a constraint-error pair
 * in which the constraint is required to be satisfied during testing such that the
 * error will be caused for the purpose of killing the mutation.
 * 
 * @author yukimula
 *
 */
public class CirMutation {
	
	/* definitions */
	/** the constraint that needs to be satisfied for killing mutation **/
	private CirConstraint constraint;
	/** the state error that is expected to occur for killing mutation **/
	private CirStateError state_error;
	/**
	 * @param constraint that needs to be satisfied for killing mutation
	 * @param state_error that is expected to occur for killing mutation
	 * @throws Exception
	 */
	protected CirMutation(CirConstraint constraint, 
			CirStateError state_error) throws Exception {
		if(constraint == null)
			throw new IllegalArgumentException("Invalid constraint: null");
		else if(state_error == null)
			throw new IllegalArgumentException("Invalid state_error: null");
		else {
			this.constraint = constraint;
			this.state_error = state_error;
		}
	}
	
	/* getters */
	/**
	 * @return create the statement where the mutation is reached
	 */
	public CirStatement get_statement() { return this.state_error.get_statement(); }
	/**
	 * @return the constraint that needs to be satisfied for killing mutation
	 */
	public CirConstraint get_constraint() { return this.constraint; }
	/**
	 * @return the state error that is expected to occur for killing mutation
	 */
	public CirStateError get_state_error() { return this.state_error; }
	@Override
	public String toString() { 
		return this.constraint.toString() + " ==> " + this.state_error.toString();
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
