package com.jcsa.jcmutest.mutant.cir2mutant.tree.anot;

import java.util.ArrayList;
import java.util.Collection;

/**
 * It denotes a unique CirAnnotation defined in the space of CirAnnotationTree,
 * which is linked to the nodes of CirAnnotation(s) that are directly subsumed
 * by this annotation in the tree space using static or dynamic evaluation.
 * 
 * @author yukimula
 *
 */
public class CirAnnotationNode {
	
	/* definitions */
	private CirAnnotationTree				tree;
	private CirAnnotation					annotation;
	private Collection<CirAnnotationNode> 	in_nodes;
	private Collection<CirAnnotationNode> 	ou_nodes;
	
	/* constructor */
	/**
	 * It creates a node that denotes the unique annotation in the space of given tree.
	 * @param tree
	 * @param annotation
	 * @throws IllegalArgumentException
	 */
	protected CirAnnotationNode(CirAnnotationTree tree, CirAnnotation annotation) throws IllegalArgumentException {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else if(annotation == null) {
			throw new IllegalArgumentException("Invalid annotation.");
		}
		else {
			this.tree = tree;
			this.annotation = annotation;
			this.in_nodes = new ArrayList<CirAnnotationNode>();
			this.ou_nodes = new ArrayList<CirAnnotationNode>();
		}
	}
	/**
	 * @param annotation
	 * @return It connects this node to the given annotation directly and returns the target node.
	 * @throws Exception
	 */
	protected CirAnnotationNode connect(CirAnnotation annotation) throws Exception {
		if(annotation == null) {
			throw new IllegalArgumentException("Invalid annotation");
		}
		else if(annotation.equals(this.annotation)) { return this; }
		else {
			CirAnnotationNode target = this.tree.get_node(annotation);
			if(!this.ou_nodes.contains(target)) {
				this.ou_nodes.add(target);
				target.in_nodes.add(this);
			}
			return target;
		}
	}
	
	/* getters */
	/**
	 * @return the tree in which the node is created
	 */
	public CirAnnotationTree get_tree() { return this.tree; }
	/**
	 * @return the annotation that is uniquely defined by this node
	 */
	public CirAnnotation get_annotation() { return this.annotation; }
	/**
	 * @return the set of nodes that directly subsume this node in the tree
	 */
	public Iterable<CirAnnotationNode> get_in_nodes() { return this.in_nodes; }
	/**
	 * @return the set of nodes that were subsumed by this node in the tree
	 */
	public Iterable<CirAnnotationNode> get_ou_nodes() { return this.ou_nodes; }
	@Override
	public String toString() { return this.annotation.toString(); }
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		else if(obj instanceof CirAnnotationNode) {
			return this.toString().equals(obj.toString());
		}
		else {
			return false;
		}
	}
	@Override
	public int hashCode() { return this.annotation.hashCode(); }
	
}
