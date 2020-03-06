package com.jcsa.jcmuta.mutant.sem2mutation.error;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;

public class StateErrorFlow {
	
	/* attributes */
	/** constraint required to hold for the error to propagate **/
	private List<SemanticAssertion> constraints;
	/** the source error that causes another in propagation **/
	private StateError source;
	/** the target error that are caused by another in propagation **/
	private StateError target;
	
	/* constructor */
	/**
	 * create a propagation flow from source to the target
	 * @param constraint
	 * @param source
	 * @param target
	 * @throws Exception
	 */
	protected StateErrorFlow(Iterable<SemanticAssertion> constraints, 
			StateError source, StateError target) throws Exception {
		if(constraints == null)
			throw new IllegalArgumentException("Invalid constraints");
		else if(source == null)
			throw new IllegalArgumentException("Invalid source as null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target as null");
		else {
			this.constraints = new ArrayList<SemanticAssertion>();
			for(SemanticAssertion constraint : constraints) {
				if(constraint.is_constraint())
					this.constraints.add(constraint);
			}
			this.source = source;
			this.target = target;
		}
	}
	
	/* getters */
	/**
	 * get the constraint required to be hold
	 * @return
	 */
	public Iterable<SemanticAssertion> get_constraints() { return this.constraints; }
	/**
	 * get the source error that causes another in the propagation
	 * @return
	 */
	public StateError get_source() { return this.source; }
	/**
	 * get the target error that caused by another in propagation
	 * @return
	 */
	public StateError get_target() { return this.target; }
	
}
