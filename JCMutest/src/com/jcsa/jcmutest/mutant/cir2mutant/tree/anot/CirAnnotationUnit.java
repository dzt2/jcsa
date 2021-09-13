package com.jcsa.jcmutest.mutant.cir2mutant.tree.anot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
public class CirAnnotationUnit {
	
	/* definitions */
	private CirAttribute 				attribute;
	private Map<CirAnnotation, Collection<CirAnnotation>> con_annotations;
	private Collection<CirAnnotation>	abs_annotations;
	
	/* constructor */
	/**
	 * It generates a unit for incorporating annotations w.r.t. the attribute
	 * @param attribute
	 * @throws Exception
	 */
	public CirAnnotationUnit(CirAttribute attribute) throws Exception {
		if(attribute == null) {
			throw new IllegalArgumentException("Invalid attribute: null");
		}
		else {
			this.attribute = attribute;
			this.con_annotations = new HashMap<CirAnnotation, Collection<CirAnnotation>>();
			this.abs_annotations = new HashSet<CirAnnotation>();
			
			Set<CirAnnotation> sym_annotations = new HashSet<CirAnnotation>();
			CirAnnotationUtils.generate_annotations(attribute, sym_annotations);
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
	public void add(SymbolProcess context) throws Exception {
		for(CirAnnotation sym_annotation : con_annotations.keySet()) {
			CirAnnotationUtils.concretize_annotations(sym_annotation, 
					context, con_annotations.get(sym_annotation));
		}
	}
	/**
	 * Summarize the abstract annotations from concrete ones
	 * @throws Exception
	 */
	public void sum() throws Exception {
		this.abs_annotations.clear();
		for(CirAnnotation sym_annotation : con_annotations.keySet()) {
			CirAnnotationUtils.summarize_annotations(sym_annotation, con_annotations.get(sym_annotation), this.abs_annotations);
		}
	}
	
}
