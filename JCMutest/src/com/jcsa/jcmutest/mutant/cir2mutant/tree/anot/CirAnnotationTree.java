package com.jcsa.jcmutest.mutant.cir2mutant.tree.anot;

import java.util.HashMap;
import java.util.Map;


/**
 * The hierarchical model of annotation defined in program and mutant execution.
 * 
 * @author yukimula
 *
 */
public class CirAnnotationTree {
	
	/* definitions */
	private Map<CirAnnotation, CirAnnotationNode> nodes;
	public CirAnnotationTree() {
		this.nodes = new HashMap<CirAnnotation, CirAnnotationNode>();
	}
	
	/* getters */
	/**
	 * @return the set of annotations defined in the tree
	 */
	public Iterable<CirAnnotation> get_annotations() { return this.nodes.keySet(); }
	/**
	 * @return the set of annotation nodes created in tree
	 */
	public Iterable<CirAnnotationNode> get_nodes() { return this.nodes.values(); }
	/**
	 * @param annotation
	 * @return the node w.r.t. the annotation unique
	 * @throws IllegalArgumentException
	 */
	public CirAnnotationNode get_node(CirAnnotation annotation) throws IllegalArgumentException {
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
	 * It constructs the nodes from the source of the annotation as given.
	 * @param annotation
	 * @throws Exception
	 */
	public void extend(CirAnnotation annotation) throws Exception {
		CirAnnotationUtil.extend_annotations(this.get_node(annotation));
	}
	
}
