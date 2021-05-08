package com.jcsa.jcmutest.mutant.sym2mutant.cond;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstance;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstances;
import com.jcsa.jcparse.parse.symbol.SymbolStateContexts;

/**
 * It preserve the accumulated status of evaluation results w.r.t. one single symbolic instance.
 * 
 * @author yukimula
 *
 */
public class SymInstanceStatus {
	
	/* definitions */
	/** the input symbolic instance being evaluated **/
	private SymInstance 		instance;
	/** the sequence of evaluation results accumulated from the input instance **/
	private List<Boolean> 		results;
	/** the collection of symbolic conditions accumulated from evaluation step **/
	private Set<SymCondition> 	conditions;
	/**
	 * create an empty status for preserving evaluation results from input instance
	 * @param instance
	 * @throws IllegalArgumentException
	 */
	public SymInstanceStatus(SymInstance instance) throws IllegalArgumentException {
		if(instance == null)
			throw new IllegalArgumentException("Invalid instance: null");
		else {
			this.instance = instance;
			this.results = new ArrayList<Boolean>();
			this.conditions = new HashSet<SymCondition>();
		}
	}
	
	/* getters */
	/**
	 * @return the input symbolic instance being evaluated
	 */
	public SymInstance get_instance() { return this.instance; }
	/**
	 * @return the sequence of evaluation results accumulated from the input instance
	 */
	public Iterable<Boolean> get_results() { return this.results; }
	/**
	 * @return the collection of symbolic conditions accumulated from evaluation step
	 */
	public Iterable<SymCondition> get_conditions() { return this.conditions; }
	/**
	 * @return the number of times to evaluate the instance in accumulation status
	 */
	public int length() { return this.results.size(); }
	/**
	 * @param k
	 * @return the kth evaluation result from the input instance
	 * @throws IndexOutOfBoundsException
	 */
	public Boolean get_result(int k) throws IndexOutOfBoundsException { return this.results.get(k); }
	
	/* setters */
	/**
	 * clear all the accumulated results from the input instance evaluations
	 */
	public void clc() { this.results.clear(); this.conditions.clear(); }
	/**
	 * evaluate the instance in given contexts and record its results into the accumulated status
	 * @param contexts
	 * @throws Exception
	 * @return the concrete instance optimized from input instance using the contexts
	 */
	public SymInstance add(SymbolStateContexts contexts) throws Exception {
		SymInstance concrete_instance = SymInstances.optimize(this.instance, contexts);
		this.results.add(concrete_instance.validate(null));
		this.conditions.addAll(SymConditions.generate(concrete_instance));
		return concrete_instance;
	}
	
	/* accumulation */
	/**
	 * @return how many times the instance was evaluated
	 */
	public int number_of_executions() { return this.results.size(); }
	/**
	 * @return how many times the instance was evaluated as true
	 */
	public int number_of_acceptions() {
		int counter = 0;
		for(Boolean result : this.results) {
			if(result != null && result.booleanValue()) {
				counter++;
			}
		}
		return counter;
	}
	/**
	 * @return how many times the instance was evaluated as false
	 */
	public int number_of_rejections() {
		int counter = 0;
		for(Boolean result : this.results) {
			if(result != null && !result.booleanValue()) {
				counter++;
			}
		}
		return counter;
	}
	/**
	 * @return whether the instance has been evaluated before
	 */
	public boolean is_executed() { return !this.results.isEmpty(); }
	/**
	 * @return whether the instance is evaluated as true at least once
	 */
	public boolean is_accepted() {
		for(Boolean result : this.results) {
			if(result != null && result.booleanValue()) {
				return true;
			}
		}
		return false;
	}
	/**
	 * @return whether the instance is always evaluated false in status
	 */
	public boolean is_rejected() {
		if(this.results.isEmpty()) {
			return false;
		}
		else {
			for(Boolean result : this.results) {
				if(result == null || result.booleanValue()) {
					return false;
				}
			}
			return true;
		}
	}
	/**
	 * @return whether the instance can be evaluated as true in evaluations.
	 */
	public boolean is_acceptable() {
		if(this.results.isEmpty()) {
			return false;
		}
		else {
			for(Boolean result : this.results) {
				if(result == null || result.booleanValue()) {
					return true;
				}
			}
			return false;
		}
	}
	
}
