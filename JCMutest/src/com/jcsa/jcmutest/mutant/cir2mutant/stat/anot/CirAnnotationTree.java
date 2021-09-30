package com.jcsa.jcmutest.mutant.cir2mutant.stat.anot;

import java.util.HashMap;
import java.util.Map;


/**
 * The hierarchical structure of CirAnnotation.
 * 
 * @author yukimula
 *
 */
public class CirAnnotationTree {
	
	/* attributes */
	private Map<CirAnnotation, CirAnnotationNode> nodes;
	public CirAnnotationTree() {
		this.nodes = new HashMap<CirAnnotation, CirAnnotationNode>();
	}
	
	/* creators */
	/**
	 * @param annotation
	 * @return It creates the node w.r.t. the annotation as given
	 * @throws Exception
	 */
	protected CirAnnotationNode new_node(CirAnnotation annotation) throws Exception {
		if(annotation == null) {
			throw new IllegalArgumentException("Invalid annotation: null");
		}
		else {
			if(!this.nodes.containsKey(annotation)) {
				this.nodes.put(annotation, new CirAnnotationNode(this, annotation));
			}
			return this.nodes.get(annotation);
		}
	}
	/**
	 * @param annotation
	 * @return it creates the node w.r.t. the annotation and extend from it
	 * @throws Exception
	 */
	public CirAnnotationNode extend_node(CirAnnotation annotation) throws Exception {
		if(annotation == null) {
			throw new IllegalArgumentException("Invalid annotation: null");
		}
		else {
			CirAnnotationNode node = this.new_node(annotation);
			CirAnnotationUtil.extend_annotations(node);
			return node;
		}
	}
	
	/* getters */
	/**
	 * @return the number of nodes within this tree
	 */
	public int size() { return this.nodes.size(); }
	/**
	 * @return the set of annotations recorded in this tree
	 */
	public Iterable<CirAnnotation> get_annotations() { return this.nodes.keySet(); }
	/**
	 * @return the set of nodes created in this tree for the given annotations
	 */
	public Iterable<CirAnnotationNode> get_nodes() { return this.nodes.values(); }
	/**
	 * @param annotation
	 * @return it gets the existing node w.r.t. the annotation as given
	 * @throws Exception
	 */
	public CirAnnotationNode get_node(CirAnnotation annotation) throws Exception {
		if(!this.nodes.containsKey(annotation)) {
			throw new IllegalArgumentException("Invalid: " + annotation);
		}
		else {
			return this.nodes.get(annotation);
		}
	}
	
}
