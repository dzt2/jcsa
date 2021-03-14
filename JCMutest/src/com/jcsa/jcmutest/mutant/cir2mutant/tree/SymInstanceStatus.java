package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirAnnotation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymInstance;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateError;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.parse.symbol.SymbolStateContexts;

/**
 * It records the accumulation collection of states evaluated from one single symbolic instance.
 * 
 * @author yukimula
 *
 */
public class SymInstanceStatus {
	
	/* definitions */
	/** abstract instance as inputs to be evaluated **/
	private SymInstance abstract_instance;
	/** collection of concrete state from source in testing **/
	private List<SymInstanceState> states;
	/** set of annotations containing in the input instance **/
	private Set<CirAnnotation> annotations;
	
	/* constructor */
	/**
	 * create an empty status w.r.t. abstract instance as inputs
	 * @param instance
	 * @throws IllegalArgumentException
	 */
	protected SymInstanceStatus(SymInstance instance) throws IllegalArgumentException {
		if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else {
			this.abstract_instance = instance;
			this.states = new ArrayList<SymInstanceState>();
			this.annotations = new HashSet<CirAnnotation>();
		}
	}
	
	/* getters */
	/**
	 * @return abstract instance as inputs to be evaluated
	 */
	public SymInstance get_instance() { return this.abstract_instance; }
	/**
	 * @return whether the instance is SymStateError
	 */
	public boolean is_state_error() { return this.abstract_instance instanceof SymStateError; }
	/**
	 * @return whether the instance is SymConstraint
	 */
	public boolean is_constraints() { return this.abstract_instance instanceof SymConstraint; }
	/**
	 * @return execution point where the instance is evaluated
	 */
	public CirExecution get_execution() { return this.abstract_instance.get_execution(); }
	/**
	 * @return collection of concrete state from source in testing
	 */
	public Iterable<SymInstanceState> get_states() { return this.states; }
	/**
	 * @return set of annotations containing in the input instance
	 */
	public Iterable<CirAnnotation> get_annotations() { return this.annotations; }
	
	/* setters */
	/**
	 * clear the concrete states evaluated in previous testing
	 */
	protected void reset() { this.states.clear(); this.annotations.clear(); }
	/**
	 * @param cir_mutations
	 * @param contexts
	 * @return perform evaluation for the instance under the given contexts
	 * @throws Exception
	 */
	protected SymInstanceState add_state(CirMutations cir_mutations, SymbolStateContexts contexts) throws Exception {
		if(cir_mutations == null)
			throw new IllegalArgumentException("Invalid cir_mutations.");
		else {
			SymInstanceState state = SymInstanceState.new_state(
					this.abstract_instance, cir_mutations, contexts);
			this.states.add(state);
			for(CirAnnotation annotation : state.get_annotations()) 
				this.annotations.add(annotation);
			return state;
		}
	}
	
	/* inference */
	/**
	 * @return the times that the instance was evaluated
	 */
	public int get_execution_times() { return this.states.size(); }
	/**
	 * @return the times that the instance evaluated as true
	 */
	public int get_acception_times() {
		int counter = 0; Boolean state_result;
		for(SymInstanceState state : this.states) {
			state_result = state.get_evaluation_result();
			if(state_result != null && state_result.booleanValue()) {
				counter++;
			}
		}
		return counter;
	}
	/**
	 * @return the times that the instance evaluated as false
	 */
	public int get_rejection_times() {
		int counter = 0; Boolean state_result;
		for(SymInstanceState state : this.states) {
			state_result = state.get_evaluation_result();
			if(state_result != null && !state_result.booleanValue()) {
				counter++;
			}
		}
		return counter;
	}
	/**
	 * @return whether the status has been evaluated
	 */
	public boolean is_executed() { return !this.states.isEmpty(); }
	/**
	 * @return whether the status has been accepted in testing
	 */
	public boolean is_accepted() {
		for(SymInstanceState state : this.states) {
			Boolean result = state.get_evaluation_result();
			if(result != null && result.booleanValue()) {
				return true;
			}
		}
		return false;
	}
	/**
	 * @return the status is acceptable if it is evaluated but not always false
	 */
	public boolean is_acceptable() {
		for(SymInstanceState state : this.states) {
			Boolean result = state.get_evaluation_result();
			if(result == null || result.booleanValue()) {
				return true;
			}
		}
		return false;
	}
	/**
	 * @return	True if any concrete state evaluated is True
	 * 			False if all concrete states evaluated as False
	 * 			null if all the concrete states are null
	 */
	public Boolean get_evaluation_result() {
		int rejection = 0, execution = 0;
		for(SymInstanceState state : this.states) {
			Boolean state_result = state.get_evaluation_result();
			if(state_result != null) {
				if(state_result.booleanValue())
					return Boolean.TRUE;
				else
					rejection++;
			}
			execution++;
		}
		if(execution > rejection) {
			return null;
		}
		else {
			return Boolean.FALSE;
		}
	}
	
}
