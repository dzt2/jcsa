package com.jcsa.jcmuta.mutant.sem2mutation.sem;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;

public class SemanticConstraint {
	
	private List<SemanticAssertion> assertions;
	protected SemanticConstraint(Iterable<SemanticAssertion> assertions) {
		if(assertions == null)
			throw new IllegalArgumentException("Invalid assertions: null");
		else {
			this.assertions = new ArrayList<SemanticAssertion>();
			for(SemanticAssertion assertion : assertions) {
				if(assertion.is_constraint()) this.assertions.add(assertion);
				else throw new IllegalArgumentException("Invalid assertion");
			}
		}
	}
	
	/**
	 * whether the constraint is empty without any assertion on the program
	 * @return
	 */
	public boolean is_empty() { return this.assertions.isEmpty(); }
	/**
	 * get the number of assertions in the constraint 
	 * @return
	 */
	public int number_of_assertions() { return this.assertions.size(); }
	/**
	 * get the constraint assertions describing the test constraint
	 * @return
	 */
	public Iterable<SemanticAssertion> get_assertions() { return assertions; }
	
}
