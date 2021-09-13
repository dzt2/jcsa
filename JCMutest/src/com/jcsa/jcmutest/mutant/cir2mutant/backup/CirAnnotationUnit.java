package com.jcsa.jcmutest.mutant.cir2mutant.backup;

import java.util.Collection;
import java.util.HashSet;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

/**
 * It maintains the symbolic annotation along with its concrete and abstract versions.
 * 
 * @author yukimula
 *
 */
public class CirAnnotationUnit {
	
	/* constructor */
	/** the symbolic annotation is taken as representative **/
	private CirAnnotation 				symbolic_annotation;
	/** the set of concrete annotations evaluated  from symbolic annotations **/
	private Collection<CirAnnotation>	concrete_annotations;
	/** the set of abstract annotations summarized from concrete annotations **/
	private Collection<CirAnnotation>	abstract_annotations;
	/**
	 * create a unit to maintain the relationship from symbolic annotation to
	 * its concretizations and summarizations.
	 * @param annotation
	 * @throws IllegalArgumentException
	 */
	protected CirAnnotationUnit(CirAnnotation annotation) throws IllegalArgumentException {
		if(annotation == null) {
			throw new IllegalArgumentException("Invalid annotation as null");
		}
		else {
			this.symbolic_annotation = annotation;
			this.concrete_annotations = new HashSet<CirAnnotation>();
			this.abstract_annotations = new HashSet<CirAnnotation>();
		}
	}
	
	/* getters */
	/**
	 * @return the symbolic annotation is taken as representative
	 */
	public CirAnnotation get_symbolic_annotation() { return this.symbolic_annotation; }
	/**
	 * @return the set of concrete annotations evaluated from symbolic annotation
	 */
	public Iterable<CirAnnotation> get_concrete_annotations() { return this.concrete_annotations; }
	/**
	 * @return the set of abstract annotations summarized from concrete annotations
	 */
	public Iterable<CirAnnotation> get_abstract_annotations() { return this.abstract_annotations; }
	
	/* setters */
	/**
	 * clear the annotations (concrete + abstract) generated from symbolic annotation
	 */
	protected void clc() { this.concrete_annotations.clear(); this.abstract_annotations.clear(); }
	/**
	 * generate the concrete annotations from symbolic one using given context
	 * @param context
	 * @throws Exception
	 */
	protected void add(SymbolProcess context) throws Exception {
		CirAnnotationUtil.concretize_annotations(symbolic_annotation, context, this.concrete_annotations);
	}
	/**
	 * generate the abstract annotations from concrete annotations anyway
	 * @throws Exception
	 */
	protected void sum() throws Exception {
		this.abstract_annotations.clear();
		CirAnnotationUtil.summarize_annotations(symbolic_annotation, concrete_annotations, abstract_annotations);
	}
	
}
