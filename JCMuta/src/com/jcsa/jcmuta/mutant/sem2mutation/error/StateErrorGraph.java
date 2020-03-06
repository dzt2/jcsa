package com.jcsa.jcmuta.mutant.sem2mutation.error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertions;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticInference;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutation;
import com.jcsa.jcparse.lang.irlang.CirNode;

/**
 * 
 * @author yukimula
 *
 */
public class StateErrorGraph {
	
	/* constructor & attributes */
	/** the semantic mutation causing errors **/
	protected SemanticMutation mutation;
	/** the infections causing initial errors **/
	protected List<StateInfection> infections;
	/** the state errors created in the graph **/
	protected List<StateError> errors;
	/**
	 * create the state error graph
	 * @param assertions
	 * @throws Exception
	 */
	protected StateErrorGraph(SemanticMutation mutation) throws Exception {
		if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else {
			this.mutation = mutation;
			this.infections = new ArrayList<StateInfection>();
			this.errors = new ArrayList<StateError>();
			this.build_infections();
		}
	}
	/**
	 * build up the initial infections
	 * @throws Exception
	 */
	private void build_infections() throws Exception {
		this.infections.clear(); this.errors.clear();
		
		for(SemanticInference inference : mutation.get_infections()) {
			/** create the constraint for the infection **/
			List<SemanticAssertion> constraints = 
					(List<SemanticAssertion>) inference.get_prev_conditions();
			
			/** dipatch the error assertions based on its location **/
			Map<CirNode, List<SemanticAssertion>> error_map = 
					new HashMap<CirNode, List<SemanticAssertion>>();
			for(SemanticAssertion assertion : inference.get_post_conditions()) {
				CirNode location = assertion.get_location();
				if(!error_map.containsKey(location)) {
					error_map.put(location, new ArrayList<SemanticAssertion>());
				}
				error_map.get(location).add(assertion);
			}
			
			/** create the valid state errors in the infections **/
			List<StateError> init_errors = new ArrayList<StateError>();
			if(error_map.isEmpty()) {
				if(!constraints.isEmpty())
					init_errors.add(new_error(new ArrayList<SemanticAssertion>()));
			}
			else {
				for(CirNode location : error_map.keySet()) {
					init_errors.add(this.new_error(error_map.get(location)));
				}
			}
			
			/** create the initial infection in the program. **/
			if(!init_errors.isEmpty()) {
				for(StateError init_error : init_errors) {
					this.infections.add(new StateInfection(constraints, init_error));
				}
			}
		}
	}
	
	/* getters */
	/**
	 * get the semantic mutation causing the initial state errors
	 * @return
	 */
	public SemanticMutation get_mutation() { return this.mutation; }
	/**
	 * get the instance to create semantic assertions
	 * @return
	 */
	public SemanticAssertions get_assertions() { return mutation.get_assertions(); }
	/**
	 * get the number of infection in the state error graph
	 * @return
	 */
	public int number_of_infections() { return this.infections.size(); }
	/**
	 * get the infections that cause the initial state errors
	 * @return
	 */
	public Iterable<StateInfection> get_infections() { return infections; }
	/**
	 * get the errors created in this graph
	 * @return
	 */
	public Iterable<StateError> get_errors() { return errors; }
	/**
	 * get the number of state errors created in this graph
	 * @return
	 */
	public int size() { return this.errors.size(); }
	
	/* setters */
	/**
	 * create a new state error in the graph
	 * @param assertions
	 * @return
	 * @throws Exception
	 */
	public StateError new_error(Iterable<SemanticAssertion> assertions) throws Exception {
		StateError error = new StateError(this, this.errors.size());
		error.set_assertions(assertions);
		StateErrorExtension.extend(error);
		this.errors.add(error);
		return error;
	}
	/**
	 * generate a propagation from source to the error with respect to the constraints.
	 * @param constraints
	 * @param source
	 * @param target
	 * @return
	 * @throws Exception
	 */
	public StateErrorFlow propagate(Iterable<SemanticAssertion> constraints, StateError source, StateError target) throws Exception {
		if(target.number_of_assertions() > 0)
			return source.propagate(constraints, target);
		else return null;
	}
	
}
