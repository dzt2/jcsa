package com.jcsa.jcmutest.mutant.cir2mutant.tree.anot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * It preserves the symbolic, concrete and abstract annotations w.r.t. a given
 * attribute (CirAttribute).
 * 
 * @author yukimula
 *
 */
public class CirAttributeState {
	
	/* definitions */
	private CirAttribute 				attribute;
	private List<Boolean>				evaluation_results;
	private Map<CirAnnotation, Collection<CirAnnotation>> con_annotations;
	private Collection<CirAnnotation>	abs_annotations;
	
	/* constructor */
	/**
	 * It generates a unit for incorporating annotations w.r.t. the attribute
	 * @param attribute
	 * @throws Exception
	 */
	public CirAttributeState(CirAttribute attribute) throws Exception {
		if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute: null");
		}
		else {
			this.attribute = attribute;
			this.con_annotations = new HashMap<CirAnnotation, Collection<CirAnnotation>>();
			this.abs_annotations = new HashSet<CirAnnotation>();
			this.evaluation_results = new ArrayList<Boolean>();
			
			Set<CirAnnotation> sym_annotations = new HashSet<CirAnnotation>();
			CirAnnotationUtil.generate_annotations(attribute, sym_annotations);
			for(CirAnnotation sym_annotation : sym_annotations) {
				this.con_annotations.put(sym_annotation, new HashSet<CirAnnotation>());
			}
		}
	}
	
	/* getters */
	public CirAttribute get_attribute() { return this.attribute; }
	public Iterable<CirAnnotation> get_sym_annotations() { return this.con_annotations.keySet(); }
	public Iterable<CirAnnotation> get_con_annotations(CirAnnotation sym_annotation) {
		if(this.con_annotations.containsKey(sym_annotation)) {
			return this.con_annotations.get(sym_annotation);
		}
		else {
			return new ArrayList<CirAnnotation>();
		}
	}
	public Iterable<CirAnnotation> get_abs_annotations() { return this.abs_annotations; }
	public Iterable<Boolean>		get_evaluation_results() { return this.evaluation_results; }
	
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
	
	/* setters */
	/**
	 * Clear the concrete and abstract annotations in the unit.
	 */
	public void clc() { 
		this.abs_annotations.clear(); 
		for(CirAnnotation sym_annotation : con_annotations.keySet()) {
			con_annotations.get(sym_annotation).clear();
		}
	}
	/**
	 * Update the concrete annotations for each symbolic one under the context
	 * @param context
	 * @throws Exception
	 */
	public Boolean add(SymbolProcess context) throws Exception {
		for(CirAnnotation sym_annotation : con_annotations.keySet()) {
			CirAnnotationUtil.concretize_annotations(sym_annotation, 
					context, con_annotations.get(sym_annotation));
		}
		Boolean result = this.attribute.evaluate(context);
		this.evaluation_results.add(result); return result;
	}
	/**
	 * Summarize the abstract annotations from concrete ones
	 * @throws Exception
	 */
	public void sum() throws Exception {
		this.abs_annotations.clear();
		for(CirAnnotation sym_annotation : con_annotations.keySet()) {
			CirAnnotationUtil.summarize_annotations(sym_annotation, con_annotations.get(sym_annotation), this.abs_annotations);
		}
	}
	
}
