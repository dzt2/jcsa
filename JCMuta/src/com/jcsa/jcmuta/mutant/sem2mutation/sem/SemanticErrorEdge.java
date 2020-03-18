package com.jcsa.jcmuta.mutant.sem2mutation.sem;

import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;

public class SemanticErrorEdge {
	
	/* constructor */
	protected SemanticConstraint constraint;
	private SemanticErrorNode source, target;
	protected SemanticErrorEdge(Iterable<SemanticAssertion> constraint_assertions,
			SemanticErrorNode source, SemanticErrorNode target) throws Exception {
		if(constraint_assertions == null)
			throw new IllegalArgumentException("Invalid constraint: null");
		else if(source == null)
			throw new IllegalArgumentException("Invalid source as null");
		else if(target == null)
			throw new IllegalArgumentException("Invalid target as null");
		else {
			this.constraint = new SemanticConstraint(constraint_assertions);
			this.source = source; this.target = target;
		}
	}
	
	/* getters */
	/**
	 * get the constraint that lead to the occurence from source to target
	 * @return
	 */
	public SemanticConstraint get_constraint() { return this.constraint; }
	/**
	 * get the source error node that causes another to occur
	 * @return
	 */
	public SemanticErrorNode get_source() { return this.source; }
	/**
	 * get the target error node that is caused by the other
	 * @return
	 */
	public SemanticErrorNode get_target() { return this.target; }
	
}
