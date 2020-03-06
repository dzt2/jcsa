package com.jcsa.jcmuta.mutant.sem2mutation.error;

import java.util.HashSet;
import java.util.Set;

import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;

/**
 * The set of constraints oriented from a set of semantic assertion as constraint.
 * @author yukimula
 *
 */
public class ConstraintSet {
	
	/* data model */
	/** set of the constraint oriented from a set of assertions as inputs **/
	private Set<SemanticAssertion> constraints;
	/** create an empty set of the constraints **/
	protected ConstraintSet(Iterable<SemanticAssertion> assertions) {
		this.constraints = new HashSet<SemanticAssertion>();
		for(SemanticAssertion assertion : assertions) {
			if(assertion.is_constraint()) {
				this.constraints.add(assertion);
			}
		}
	}
	/**
	 * get the number of constrants
	 * @return
	 */
	public int size() { return this.constraints.size(); }
	/**
	 * get the semantic assertions describing the constraints
	 * @return
	 */
	public Iterable<SemanticAssertion> get_constraints() { return this.constraints; }
	
}
