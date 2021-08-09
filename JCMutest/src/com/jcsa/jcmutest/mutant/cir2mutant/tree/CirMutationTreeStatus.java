package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	/** the set of annotations generated for each evaluation **/
	private Set<CirAnnotation>	concrete_annotations;
	/** the set of abstract annotations accumulated from concrete set **/
	private Set<CirAnnotation> 	abstract_annotations;

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
			this.concrete_annotations = new HashSet<CirAnnotation>();
			this.abstract_annotations = new HashSet<CirAnnotation>();
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
	/**
	 * @return the set of annotations (concrete) generated from the evaluation and analysis
	 */
	public Iterable<CirAnnotation> get_concrete_annotations() { return this.concrete_annotations; }
	/**
	 * @return the set of abstract annotations summarized from the concrete annotations
	 */
	public Iterable<CirAnnotation> get_abstract_annotations() { return this.abstract_annotations; }

	/* setters */
	/**
	 * clear the accumulated results and concrete attributes in the evaluation history
	 */
	protected void clc() {
		this.evaluation_results.clear();
		this.concrete_attributes.clear();
		this.abstract_annotations.clear();
		this.concrete_annotations.clear();
	}
	/**
	 * evaluate the source attribute and generate its concrete results in accumulation
	 * @param context
	 * @throws Exception
	 */
	protected Boolean add(SymbolProcess context) throws Exception {
		Boolean result = attribute.evaluate(context);
		this.evaluation_results.add(result);
		CirAttribute concrete_attribute = attribute.optimize(context);
		this.concrete_attributes.add(concrete_attribute);
		if(concrete_attribute.is_constraint()) {
			CirAnnotationUtil.util.generate_annotations(this.attribute, null, this.concrete_annotations);
		}
		else {
			CirAnnotationUtil.util.generate_annotations(concrete_attribute, context, this.concrete_annotations);
		}
		return result;
	}
	/**
	 * generate the abstract annotations summarized from the concrete ones
	 * @throws Exception
	 */
	protected void sum() throws Exception {
		this.abstract_annotations.clear();
		CirAnnotationUtil.util.summarize_annotations(concrete_annotations, abstract_annotations);
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
	 * @return the number of attribute being accepted or likely be acceptable
	 */
	public int number_of_acceptable() {
		int counter = 0;
		for(Boolean result : this.evaluation_results) {
			if(result == null || result.booleanValue()) {
				counter++;
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
