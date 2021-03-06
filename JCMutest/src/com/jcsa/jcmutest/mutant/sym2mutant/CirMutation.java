package com.jcsa.jcmutest.mutant.sym2mutant;

import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class CirMutation {
	
	/* definitions */
	/** the constraint that needs to be satisfied for killing mutation **/
	private SymConstraint constraint;
	/** the state error that is expected to occur for killing mutation **/
	private SymStateError state_error;
	/**
	 * @param constraint that needs to be satisfied for killing mutation
	 * @param state_error that is expected to occur for killing mutation
	 * @throws Exception
	 */
	public CirMutation(SymConstraint constraint, 
			SymStateError state_error) throws Exception {
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
	public CirExecution get_execution() { return this.state_error.get_execution(); }
	/**
	 * @return create the statement where the mutation is reached
	 */
	public CirStatement get_statement() { return this.state_error.get_statement(); }
	/**
	 * @return the constraint that needs to be satisfied for killing mutation
	 */
	public SymConstraint get_constraint() { return this.constraint; }
	/**
	 * @return the state error that is expected to occur for killing mutation
	 */
	public SymStateError get_state_error() { return this.state_error; }
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
