package com.jcsa.jcmutest.mutant.cir2mutant.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirAnnotation;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymInstance;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymInstanceUtils;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateError;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.parse.symbol.SymbolStateContexts;

/**
 * 	It records the execution state for one single symbolic instance (either state error or constraint), where 
 * 	the following parameters are evaluated and recorded during testing.<br>
 * 	<br>
 * 	<code>
 * 		<b>s_instance</b>: 	either constraint or state error as source being evaluated at some location;<br>
 * 		<b>execution</b>: 	the execution point in control flow graph where instance is defined;<br>
 * 		<b>c_instances</b>: the collection of concrete instances computed from source instance;<br>
 * 		<b>annotations</b>: the collection of annotations to abstract the description of every concrete instance;<br>
 * 		<b>evaluations</b>:	the collection of Boolean results for each time s_instance evaluated in testing.<br>
 * 	</code>
 * 	<br>
 * 	@author yukimula
 *
 */
public class SymInstanceState {
	
	/* definitions */
	/** either constraint or state error as source being evaluated at some location; **/
	private SymInstance 				s_instance;
	/** the collection of concrete instances computed from source instance; **/
	private Collection<SymInstance> 	c_instances;
	/** the collection of annotations to abstract the description of every concrete instance; **/
	private Collection<CirAnnotation> 	annotations;
	/** the collection of Boolean results for each time s_instance evaluated in testing. **/
	private Collection<Boolean>			evaluations;
	
	/* constructor */
	/**
	 * create an empty state recording the evaluation of source instance
	 * @param instance
	 * @throws IllegalArgumentException
	 */
	protected SymInstanceState(SymInstance instance) throws IllegalArgumentException {
		if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else {
			this.s_instance = instance;
			this.c_instances = new ArrayList<SymInstance>();
			this.annotations = new HashSet<CirAnnotation>();
			this.evaluations = new ArrayList<Boolean>();
		}
	}
	
	/* getters */
	/**
	 * @return either constraint or state error as source being evaluated at some location;
	 */
	public SymInstance get_source_instance() { return this.s_instance; }
	/**
	 * @return whether the instance being evaluated is a constraint
	 */
	public boolean is_constraint() { return this.s_instance instanceof SymConstraint; }
	/**
	 * @return whether the instance being evaluated is a state error
	 */
	public boolean is_state_error() { return this.s_instance instanceof SymStateError; }
	/**
	 * @return the execution point in control flow graph where instance should be evaluated;
	 */
	public CirExecution get_execution() { return this.s_instance.get_execution(); }
	/**
	 * @return the collection of concrete instances computed from source instance;
	 */
	public Iterable<SymInstance> get_concrete_instances() { return this.c_instances; }
	/**
	 * @return the collection of Boolean results for each time s_instance evaluated in testing.
	 */
	public Iterable<Boolean>	 get_evaluation_results() { return this.evaluations; }
	/**
	 * @return the collection of annotations to abstract the description of every concrete instance;
	 */
	public Iterable<CirAnnotation> get_instance_annotations() { return this.annotations; }
	
	/* setters */
	/**
	 * clear all the concrete instances, evaluation results and annotations generated
	 */
	protected void reset() {
		this.c_instances.clear();
		this.evaluations.clear();
		this.annotations.clear();
	}
	/**
	 * @param cir_mutations
	 * @param contexts
	 * @return	evaluate the source instance at the point using given contexts
	 * @throws Exception
	 */
	protected Boolean evaluate(CirMutations cir_mutations, SymbolStateContexts contexts) throws Exception {
		/* determine the validation result of the symbolic instance */
		Boolean result;
		if(this.s_instance instanceof SymConstraint) {
			SymConstraint constraint = (SymConstraint) this.s_instance;
			constraint = cir_mutations.optimize(constraint, contexts);
			this.c_instances.add(constraint);
			this.annotations.addAll(SymInstanceUtils.annotations(constraint));
			result = constraint.validate(null);
		}
		else if(this.s_instance instanceof SymStateError) {
			SymStateError state_error = (SymStateError) this.s_instance;
			state_error = cir_mutations.optimize(state_error, contexts);
			this.c_instances.add(state_error);
			this.annotations.addAll(SymInstanceUtils.annotations(state_error));
			result = state_error.validate(null);
		}
		else {
			throw new IllegalArgumentException("Unable to evaluate instance...");
		}
		this.evaluations.add(result);
		return result;
	}
	
	/* summary */
	/**
	 * @return the times that the instance was evaluated
	 */
	public int get_execution_times() { return this.evaluations.size(); }
	/**
	 * @return the times that the instance evaluated as true
	 */
	public int get_acception_times() {
		int acception = 0;
		for(Boolean evaluation : this.evaluations) {
			if(evaluation != null) {
				if(evaluation.booleanValue()) {
					acception++;
				}
			}
		}
		return acception;
	}
	/**
	 * @return the times that the instance evaluated as false
	 */
	public int get_rejection_times() {
		int rejection = 0;
		for(Boolean evaluation : this.evaluations) {
			if(evaluation != null) {
				if(!evaluation.booleanValue()) {
					rejection++;
				}
			}
		}
		return rejection;
	}
	/**
	 * @return whether the status has been evaluated
	 */
	public boolean is_executed() { return !this.evaluations.isEmpty(); }
	/**
	 * @return whether the status has been accepted in testing
	 */
	public boolean is_accepted() {
		for(Boolean evaluation : this.evaluations) {
			if(evaluation != null) {
				if(evaluation.booleanValue()) {
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * @return the status is acceptable if it is evaluated but not always false
	 */
	public boolean is_acceptable() {
		for(Boolean evaluation : this.evaluations) {
			if(evaluation == null) {
				return true;
			}
			else if(evaluation.booleanValue()) {
				return true;
			}
		}
		return false;
	}
	
}
