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
 * It maintains the set of CirAnnotation generated, concretized and summarized
 * from a given CirAttribute.
 * 
 * @author yukimula
 *
 */
public class CirAttributeUnit {
	
	/* definitions */
	private CirAttribute 				attribute;
	private List<Boolean>				evaluation_results;
	private Map<CirAnnotation, Collection<CirAnnotation>> annotation_maps;
	private Collection<CirAnnotation>	abs_annotations;
	
	/* constructor */
	/**
	 * It constructs a data unit to preserve the annotations referring to a
	 * given attribute.
	 * @param attribute
	 * @throws Exception
	 */
	public CirAttributeUnit(CirAttribute attribute) throws Exception {
		if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute: null");
		}
		else {
			this.attribute = attribute;
			this.evaluation_results = new ArrayList<Boolean>();
			this.annotation_maps = new HashMap<
					CirAnnotation, Collection<CirAnnotation>>();
			this.abs_annotations = new HashSet<CirAnnotation>();
			this.initialize();	/* generate symbolic annotations */
		}
	}
	/**
	 * It initializes the symbolic annotations and maps
	 * @throws Exception
	 */
	private void initialize() throws Exception {
		Set<CirAnnotation> sym_annotations = new HashSet<CirAnnotation>();
		CirAnnotationUtil.generate_annotations(this.attribute, sym_annotations);
		this.annotation_maps.clear();
		this.abs_annotations.clear();
		for(CirAnnotation sym_annotation : sym_annotations) {
			this.annotation_maps.put(sym_annotation, new ArrayList<CirAnnotation>());
		}
	}
	
	/* getters */
	/**
	 * @return the attribute that the unit describes and evaluates
	 */
	public CirAttribute get_attribute() { return this.attribute; }
	/**
	 * @return the sequence of evaluation results of the attribute during testing.
	 */
	public Iterable<Boolean>		get_evaluation_results() { return this.evaluation_results; }
	/**
	 * @return the set of symbolic annotations generated from the input attribute
	 */
	public Iterable<CirAnnotation> get_sym_annotations() { return this.annotation_maps.keySet(); }
	/**
	 * @param sym_annotation
	 * @return the set of concrete annotations produced from input symbolic annotation if it is defined
	 */
	public Iterable<CirAnnotation> get_con_annotations(CirAnnotation sym_annotation) {
		if(this.annotation_maps.containsKey(sym_annotation)) {
			return this.annotation_maps.get(sym_annotation);
		}
		else {
			return new ArrayList<CirAnnotation>();
		}
	}
	/**
	 * @return the set of abstract annotations summarized from the concrete annotations of each symbolic
	 */
	public Iterable<CirAnnotation> get_abs_annotations() { return this.abs_annotations; }
	
	/* results */
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
	 * It clears the evaluation results and generated annotations in the attribute
	 */
	public void clc() { 
		this.abs_annotations.clear();
		for(CirAnnotation sym_annotation : this.annotation_maps.keySet()) {
			this.annotation_maps.get(sym_annotation).clear();
		}
		this.evaluation_results.clear();
	}
	/**
	 * @param context
	 * @return true (satisfied), false (not-satisfied), null (unknown).
	 * @throws Exception
	 */
	public Boolean add(SymbolProcess context) throws Exception {
		/* 1. generate the concrete annotations from context */
		for(CirAnnotation sym_annotation : this.annotation_maps.keySet()) {
			CirAnnotationUtil.concretize_annotations(sym_annotation, 
					context, this.annotation_maps.get(sym_annotation));
		}
		
		/* 2. record the evaluation result of the attribute */
		Boolean result = this.attribute.evaluate(context);
		this.evaluation_results.add(result); return result;
	}
	/**
	 * It summarizes the concrete annotations to produce their abstract annotations.
	 * @throws Exception
	 */
	public void sum() throws Exception {
		this.abs_annotations.clear();
		for(CirAnnotation sym_annotation : this.annotation_maps.keySet()) {
			CirAnnotationUtil.summarize_annotations(sym_annotation, 
					this.annotation_maps.get(sym_annotation), this.abs_annotations);
		}
	}
	
}
