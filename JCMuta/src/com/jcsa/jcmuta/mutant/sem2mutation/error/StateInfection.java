package com.jcsa.jcmuta.mutant.sem2mutation.error;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;

public class StateInfection {
	
	/* constructor */
	private List<SemanticAssertion> constraints;
	private StateError state_error;
	protected StateInfection(Iterable<SemanticAssertion> constraints, StateError state_error) throws Exception {
		if(constraints == null)
			throw new IllegalArgumentException("Invalid constraints: null");
		else if(state_error == null)
			throw new IllegalArgumentException("Invalid state error: null");
		else {
			this.constraints = new ArrayList<SemanticAssertion>();
			for(SemanticAssertion constraint : constraints) {
				if(constraint.is_constraint())
					this.constraints.add(constraint);
			}
			this.state_error = state_error;
		}
	}
	
	/* getters */
	/**
	 * get the constraint required to hold for infection the error
	 * @return
	 */
	public Iterable<SemanticAssertion> get_constraints() { return this.constraints; }
	/**
	 * the initial error caused by the software fault
	 * @return
	 */
	public StateError get_state_error() { return this.state_error; }
	
}
