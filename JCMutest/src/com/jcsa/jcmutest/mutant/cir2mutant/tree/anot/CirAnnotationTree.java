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
	public CirAnnotationNode get_node(CirAnnotation annotation) throws Exception {
		if(annotation == null) {
			throw new IllegalArgumentException("Invalid annotation: null");
		}
		else {
			if(!this.nodes.containsKey(annotation)) {
				CirAnnotationNode node = new CirAnnotationNode(this, annotation);
				this.nodes.put(annotation, node);
				CirAnnotationUtil.extend_annotations(node);
			}
			return this.nodes.get(annotation);
		}
	}
	
}
