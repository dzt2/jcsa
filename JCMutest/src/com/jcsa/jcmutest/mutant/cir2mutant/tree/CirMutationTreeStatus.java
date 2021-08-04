package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * It records the status accumultated in evaluation of the tree node.
 * 
 * @author yukimula
 *
 */
public class CirMutationTreeStatus {
	
	/* attributes */
	/** the source attribute to be evaluated and accumulated **/
	private CirAttribute 		attribute;
	/** the set of evaluation results using dynamic analysis **/
	private List<Boolean> 		evaluation_results;
	/** the set of concrete attributes generated from source **/
	private List<CirAttribute> 	concrete_attributes;
	
	/* constructor */
	/**
	 * create a status w.r.t. the tree node of particular attribute
	 * @param attribute
	 * @throws IllegalArgumentException
	 */
	protected CirMutationTreeStatus(CirAttribute attribute) throws IllegalArgumentException {
		if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute: null");
		}
		else {
			this.attribute = attribute;
			this.evaluation_results = new ArrayList<Boolean>();
			this.concrete_attributes = new ArrayList<CirAttribute>();
		}
	}
	
	/* getters */
	/**
	 * @return the source attribute to be evaluated and accumulated
	 */
	public CirAttribute get_attribute() { return this.attribute; }
	/**
	 * @return the set of evaluation results using dynamic analysis
	 */
	public Iterable<Boolean> get_evaluation_results() { return this.evaluation_results; }
	/**
	 * @return the set of concrete attributes generated from source
	 */
	public Iterable<CirAttribute> get_concrete_attributes() { return this.concrete_attributes; }
	
	/* setters */
	/**
	 * clear the accumulated results and concrete attributes in the evaluation history
	 */
	protected void clc() { this.evaluation_results.clear(); this.concrete_attributes.clear(); }
	/**
	 * evaluate the source attribute and generate its concrete results in accumulation
	 * @param context
	 * @throws Exception
	 */
	protected void add(SymbolProcess context) throws Exception {
		this.evaluation_results.add(attribute.evaluate(context));
		this.concrete_attributes.add(attribute.optimize(context));
	}
	
	/* counters */
	/**
	 * @return the number of attribute being executed in dynamic analysis
	 */
	public int number_of_executions() { return this.evaluation_results.size(); }
	/**
	 * @return the number of attribute being accepted in dynamic analysis
	 */
	public int number_of_acceptions() {
		int counter = 0;
		for(Boolean result : this.evaluation_results) {
			if(result != null) {
				if(result.booleanValue()) {
					counter++;
				}
			}
		}
		return counter;
	}
	/**
	 * @return the number of attribute being rejected in dynamic analysis
	 */
	public int number_of_rejections() {
		int counter = 0;
		for(Boolean result : this.evaluation_results) {
			if(result != null) {
				if(!result.booleanValue()) {
					counter++;
				}
			}
		}
		return counter;
	}
	/**
	 * @return whether the attribute in the node has been evaluated before
	 */
	public boolean is_executed() { return !this.evaluation_results.isEmpty(); }
	/**
	 * @return whether the attribute in the node has been accepted before.
	 */
	public boolean is_accepted() {
		for(Boolean result: this.evaluation_results) {
			if(result != null && result) {
				return true;
			}
		}
		return false;
	}
	/**
	 * @return whether the attribute in the node has always been rejected.
	 */
	public boolean is_rejected() {
		for(Boolean result: this.evaluation_results) {
			if(result == null) {
				return false;
			}
			else if(result) {
				return false;
			}
		}
		return true;
	}
	/**
	 * @return whether the attribute in the node can be accepted before.
	 */
	public boolean is_acceptable() {
		for(Boolean result: this.evaluation_results) {
			if(result == null || result.booleanValue()) {
				return true;
			}
		}
		return false;
	}
	
}