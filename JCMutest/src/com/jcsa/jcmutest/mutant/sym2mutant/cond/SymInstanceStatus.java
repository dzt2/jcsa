package com.jcsa.jcmutest.mutant.sym2mutant.cond;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstance;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

public class SymInstanceStatus {
	
	/* definitions */
	/** the tree with the instance and evalauted condition **/
	private SymConditionTree condition_tree;
	/** the sequence of accumulated evaluation results **/
	private List<Boolean> results; 
	/**
	 * create a status with recorded structure dependence path
	 * @param instance
	 * @throws Exception
	 */
	public SymInstanceStatus(SymInstance instance) throws Exception {
		this.results = new ArrayList<Boolean>();
		this.condition_tree = SymConditionTree.new_tree(instance);
	}
	
	/* evaluation */
	/**
	 * clear the accumulated evaluation results
	 */
	public void clear_results() { this.results.clear(); }
	/**
	 * @param process
	 * @return
	 * @throws Exception
	 */
	public Boolean evaluate(SymbolProcess process) throws Exception {
		Boolean result = this.condition_tree.
				get_instance().validate(process);
		this.results.add(result);	
		return result;
	}
	/**
	 * @return [execute, accept, reject]
	 */
	public int[] count_results() {
		int accepts = 0, rejects = 0;
		for(Boolean result : this.results) {
			if(result != null) {
				if(result.booleanValue()) {
					accepts++;
				}
				else {
					rejects++;
				}
			}
		}
		return new int[] { this.results.size(), accepts, rejects };
	}
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
	
	/* getters  */
	/**
	 * @return the instance to be evaluated in the status corpus
	 */
	public SymInstance get_instance() { return this.condition_tree.get_instance(); }
	/**
	 * @return the execution point where the tree's instance is evaluated on
	 */
	public CirExecution get_execution() { return this.get_instance().get_execution(); }
	/**
	 * @return the set of symbolic conditions defined in the hierarchy
	 */
	public Iterable<SymCondition> get_conditions() { return this.condition_tree.get_conditions(); }
	/**
	 * @return the symbolic conditions are organized in hierarchical way
	 */
	public SymConditionTree get_condition_hierarchy() { return this.condition_tree; }
	
}
