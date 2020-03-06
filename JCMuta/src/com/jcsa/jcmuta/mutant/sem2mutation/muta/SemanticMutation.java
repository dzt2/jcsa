package com.jcsa.jcmuta.mutant.sem2mutation.muta;

import java.util.HashSet;
import java.util.Set;

import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * In mutation testing, the semantic mutation refers to a set of constraints and state errors
 * that cause each other for the purpose of software failure.
 * @author yukimula
 *
 */
public class SemanticMutation {
	
	/* constructor */
	private SemanticAssertions assertions;
	private SemanticAssertion reachability;
	public SemanticMutation(CirStatement statement) throws Exception {
		this.assertions = new SemanticAssertions();
		this.reachability = this.assertions.cover(statement);
	}
	
	/* getters */
	/**
	 * get the semantic assertion graph
	 * @return
	 */
	public SemanticAssertions get_assertions() { return this.assertions; }
	/**
	 * get the constraint of covering the faulty statement
	 * @return
	 */
	public SemanticAssertion get_reachability() { return this.reachability; }
	/**
	 * get the state infection from constraints to the state errors.
	 * @return
	 */
	public Iterable<SemanticInference> get_infections() { return this.reachability.get_ou_inferences(); }
	/**
	 * get the number of state infection in the semantic mutation
	 * @return
	 */
	public int number_of_infections() { return this.reachability.get_ou_degree(); }
	
	/* setters */
	/**
	 * cover; constraints ==> state_errors+
	 * @param constraints
	 * @param state_errors
	 * @return
	 * @throws Exception
	 */
	protected SemanticInference infect(
			SemanticAssertion[] constraints, 
			SemanticAssertion[] state_errors) throws Exception {
		if(constraints == null)
			throw new IllegalArgumentException("Invalid constraints: null");
		else if(state_errors == null || state_errors.length == 0)
			throw new IllegalArgumentException("Invalid state_error: null");
		else {
			Set<SemanticAssertion> constraint_set = new HashSet<SemanticAssertion>();
			Set<SemanticAssertion> state_error_set = new HashSet<SemanticAssertion>();
			
			constraint_set.add(this.reachability);
			for(SemanticAssertion constraint : constraints) {
				if(!constraint.is_constraint())
					throw new IllegalArgumentException("Not constraint");
				else if(constraint.get_function() == ConstraintFunction.impossible)
					throw new IllegalArgumentException("Invalid constraint as impossible");
				else if(constraint.get_function() != ConstraintFunction.all_possible)
					constraint_set.add(constraint);	/* ignore the TRUE constraint */
			}
			
			for(SemanticAssertion state_error: state_errors) {
				if(!state_error.is_state_error())
					throw new IllegalArgumentException("Not state error");
				else state_error_set.add(state_error);
			}
			
			return this.assertions.infer(constraint_set, state_error_set);
		}
	}
	
}
