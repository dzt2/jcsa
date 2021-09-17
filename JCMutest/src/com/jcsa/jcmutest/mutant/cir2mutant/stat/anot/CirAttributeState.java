package com.jcsa.jcmutest.mutant.cir2mutant.stat.anot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * It manages the symbolic (representative), concrete and abstract annotations
 * for evaluating and defining the state of a CirAttribute.
 * 
 * @author yukimula
 *
 */
public class CirAttributeState {
	
	/* definitions */
	private CirAttribute									attribute;
	private List<Boolean>									results;
	private Collection<CirAnnotation>						sym_annotations;
	private Map<CirAnnotation, Collection<CirAnnotation>>	con_annotations;
	private Collection<CirAnnotation>						abs_annotations;
	
	/* constructor */
	/**
	 * It creates an empty state for given attribute
	 * @param attribute
	 * @throws Exception
	 */
	public CirAttributeState(CirAttribute attribute) throws Exception {
		if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute: null");
		}
		else {
			this.attribute = attribute;
			this.results = new ArrayList<Boolean>();
			this.sym_annotations = new HashSet<CirAnnotation>();
			this.con_annotations = new HashMap<CirAnnotation, Collection<CirAnnotation>>();
			this.abs_annotations = new HashSet<CirAnnotation>();
			
			CirAnnotationUtils.generate_annotations(this.attribute, this.sym_annotations);
			for(CirAnnotation sym_annotation : this.sym_annotations) {
				this.con_annotations.put(sym_annotation, new ArrayList<CirAnnotation>());
			}
		}
	}
	
	/* getters */
	/**
	 * @return the attribute of the state defines
	 */
	public CirAttribute get_attribute() { return this.attribute; }
	/**
	 * @return the evaluation results of the attribute
	 */
	public Iterable<Boolean> get_results() { return this.results; }
	/**
	 * @return the set of symbolic (representative) annotations from attribute
	 */
	public Iterable<CirAnnotation> get_sym_annotations() { return this.sym_annotations; }
	/**
	 * @param sym_annotation
	 * @return the set of concrete annotations generated from representative ones
	 */
	public Iterable<CirAnnotation> get_con_annotations(CirAnnotation sym_annotation) {
		if(this.con_annotations.containsKey(sym_annotation)) {
			return this.con_annotations.get(sym_annotation);
		}
		else {
			return new ArrayList<CirAnnotation>();
		}
	}
	/**
	 * @return the set of abstract annotations summarized from attribute and conrete ones
	 */
	public Iterable<CirAnnotation> get_abs_annotations() { return this.abs_annotations; }
	
	/* results */
	/**
	 * @return the number of attribute being executed in dynamic analysis
	 */
	public int number_of_executions() { return this.results.size(); }
	/**
	 * @return the number of attribute being accepted in dynamic analysis
	 */
	public int number_of_acceptions() {
		int counter = 0;
		for(Boolean result : this.results) {
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
		for(Boolean result : this.results) {
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
		for(Boolean result : this.results) {
			if(result == null || result.booleanValue()) {
				counter++;
			}
		}
		return counter;
	}
	/**
	 * @return whether the attribute in the node has been evaluated before
	 */
	public boolean is_executed() { return !this.results.isEmpty(); }
	/**
	 * @return whether the attribute in the node has been accepted before.
	 */
	public boolean is_accepted() {
		for(Boolean result: this.results) {
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
		for(Boolean result: this.results) {
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
		for(Boolean result: this.results) {
			if(result == null || result.booleanValue()) {
				return true;
			}
		}
		return false;
	}
	
	/* evaluate */
	/**
	 * It clears the set of annotations and evaluation results in state
	 */
	public void clc() {
		this.abs_annotations.clear();
		for(CirAnnotation sym_annotation : this.con_annotations.keySet()) {
			this.con_annotations.get(sym_annotation).clear();
		}
		this.results.clear();
	}
	/**
	 * @param context
	 * @return It concretizes the annotations and evaluate attribute.
	 * @throws Exception
	 */
	public Boolean add(SymbolProcess context) throws Exception {
		for(CirAnnotation sym_annotation : this.con_annotations.keySet()) {
			CirAnnotationUtils.concretize_annotations(sym_annotation, 
					context, this.con_annotations.get(sym_annotation));
		}
		Boolean result = this.attribute.evaluate(context);
		this.results.add(result); 
		return result;
	}
	/**
	 * It summarizes the concrete annotations in the concrete annotation
	 * @throws Exception
	 */
	public void sum() throws Exception {
		this.abs_annotations.clear();
		for(CirAnnotation sym_annotation : this.con_annotations.keySet()) {
			CirAnnotationUtils.summarize_annotations(sym_annotation, 
					this.con_annotations.get(sym_annotation), this.abs_annotations);
		}
	}
	
}
