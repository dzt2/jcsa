package com.jcsa.jcmutest.mutant.sym2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstance;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.util.SymConditionUtils;
import com.jcsa.jcmutest.mutant.sym2mutant.util.SymInstanceUtils;
import com.jcsa.jcparse.parse.symbol.SymbolStateContexts;

/**
 * The accumulated evaluation results w.r.t. one single instance by performing multiple evaluations.
 * 
 * @author yukimula
 *
 */
public class SymInstanceStatus {
	
	/* definitions */
	/** abstract symbolic instance being evaluated **/
	private SymInstance abstract_instance;
	/** the sequence of concrete instances being generated from abstract one **/
	private List<SymInstance> concrete_instances;
	/** the sequence of evaluation results for each concrete instance in testing **/
	private List<Boolean> 	evaluation_results;
	/** the set of symbolic conditions being  **/
	private Collection<SymCondition> conditions;
	/**
	 * create an accululated status for specified instance
	 * @param instance
	 * @throws Exception
	 */
	protected SymInstanceStatus(SymInstance instance) throws Exception {
		if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else {
			this.abstract_instance = instance;
			this.concrete_instances = new ArrayList<SymInstance>();
			this.conditions = new HashSet<SymCondition>();
		}
	}
	
	/* getters */
	/**
	 * @return whether the abstract instance is a constraint
	 */
	public boolean is_constraint() { return this.abstract_instance instanceof SymConstraint; }
	/**
	 * @return whether the abstract instance is a state error
	 */
	public boolean is_state_error() { return this.abstract_instance instanceof SymStateError; }
	/**
	 * @return abstract symbolic instance being evaluated
	 */
	public SymInstance get_abstract_instance() { return this.abstract_instance; }
	/**
	 * @return the sequence of concrete instances being generated from abstract one
	 */
	public Iterable<SymInstance> get_concrete_instances() { return this.concrete_instances; }
	/**
	 * @return the sequence of evaluation results for each concrete instance in testing
	 */
	public Iterable<Boolean> get_evaluation_results() { return this.evaluation_results; }
	/**
	 * @return the number of concrete instances accumulated within the status
	 */
	public int length() { return this.concrete_instances.size(); }
	/**
	 * @param k
	 * @return the kth concrete instance w.r.t. abstract instance under testing
	 * @throws IndexOutOfBoundsException
	 */
	public SymInstance get_concrete_instance(int k) throws IndexOutOfBoundsException { return this.concrete_instances.get(k); }
	/**
	 * @param k
	 * @return the evaluation result of the kth concrete result
	 * @throws Exception
	 */
	public Boolean get_evaluation_result(int k) throws IndexOutOfBoundsException { return this.evaluation_results.get(k); }
	/**
	 * @return the set of symbolic conditions within the status
	 */
	public Iterable<SymCondition> get_conditions() { return this.conditions; }
	
	/* infer */
	/**
	 * @return how many times the instance is executed in accumulation status
	 */
	public int get_execution_times() { return this.evaluation_results.size(); }
	/**
	 * @return how many times the instance is accepted in accumulation status
	 */
	public int get_acception_times() {
		int counter = 0;
		for(Boolean result : this.evaluation_results) {
			if(result != null && result.booleanValue())
				counter++;
		}
		return counter;
	}
	/**
	 * @return how many times the instance is rejected in accumulation status
	 */
	public int get_rejection_times() {
		int counter = 0;
		for(Boolean result : this.evaluation_results) {
			if(result != null && !result.booleanValue())
				counter++;
		}
		return counter;
	}
	/**
	 * @return whether the instance is satisfied or not or unknown in accumulation
	 */
	public Boolean get_evaluation_result() {
		int executions = 0, acceptions = 0, rejections = 0;
		for(Boolean result : this.evaluation_results) {
			if(result != null) {
				if(result.booleanValue())
					acceptions++;
				else
					rejections++;
			}
			executions++;
		}
		
		if(executions == 0)
			return null;
		else if(acceptions > 0)
			return Boolean.TRUE;
		else if(rejections == executions)
			return Boolean.FALSE;
		else
			return null;
	}
	
	/* setters */
	/**
	 * remove all the records from the status
	 */
	public void clear() { 
		this.concrete_instances.clear(); 
		this.evaluation_results.clear();
		this.conditions.clear();
	}
	/**
	 * append a concrete result using the contexts into accumulation
	 * @param contexts
	 * @throws Exception
	 */
	public void append(SymbolStateContexts contexts) throws Exception {
		SymInstance concrete_instance = SymInstanceUtils.optimize(this.abstract_instance, contexts);
		this.concrete_instances.add(concrete_instance);
		this.evaluation_results.add(concrete_instance.validate(null));
		this.conditions.addAll(SymConditionUtils.sym_conditions(concrete_instance));
	}
	
}
