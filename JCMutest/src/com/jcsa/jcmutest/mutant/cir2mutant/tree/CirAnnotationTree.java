package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.HashMap;
import java.util.Map;

/**
 * It describes the hierarchical relationships in annotation node.
 * 
 * @author yukimula
 *
 */
public class CirAnnotationTree {
	
	/* definition */
	private Map<CirAnnotation, CirAnnotationNode> nodes;
	public CirAnnotationTree() {
		this.nodes = new HashMap<CirAnnotation, CirAnnotationNode>();
	}
	
	/* getters */
	/**
	 * @return the set of annotations of which nodes have been created
	 */
	public Iterable<CirAnnotation> get_annotations() { return this.nodes.keySet(); }
	/**
	 * @param annotation
	 * @return whether there is node w.r.t. the input annotation
	 */
	public boolean has_annotation(CirAnnotation annotation) {
		return this.nodes.containsKey(annotation);
	}
	/**
	 * @return the set of nodes that have been created from the tree
	 */
	public Iterable<CirAnnotationNode> get_nodes() { return this.nodes.values(); }
	/**
	 * @param annotation
	 * @return the node w.r.t. the input annotation or null if not defined
	 */
	public CirAnnotationNode get_node(CirAnnotation annotation) {
		if(this.nodes.containsKey(annotation)) {
			return this.nodes.get(annotation);
		}
		else {
			return null;
		}
	}
	/**
	 * @param annotation
	 * @return the unique node w.r.t. the annotation in the tree
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
	 * It constructs the annotation nodes in the tree
	 * @param annotation
	 * @throws Exception
	 */
	public void extend_from(CirAnnotation annotation) throws Exception {
		if(annotation == null) {
			throw new IllegalArgumentException("Invalid annotation: null");
		}
		else {
			CirAnnotationNode source = this.new_node(annotation);
			CirAnnotationUtils.extend_annotations(source);
		}
	}
	
}
