package com.jcsa.jcmutest.mutant.cir2mutant.stat.anot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * It specifies a unique node referring to a CirAnnotation in the tree space.
 * 
 * @author yukimula
 *
 */
public class CirAnnotationNode {
	
	/* attributes */
	private CirAnnotationTree 		tree;
	private CirAnnotation			annotation;
	private List<CirAnnotationNode>	in_nodes;
	private List<CirAnnotationNode>	ou_nodes;
	
	/* constructor */
	/**
	 * It creates a node w.r.t. the given annotation in the tree
	 * @param tree
	 * @param annotation
	 * @throws IllegalArgumentException
	 */
	protected CirAnnotationNode(CirAnnotationTree tree,
			CirAnnotation annotation) throws IllegalArgumentException {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else if(annotation == null) {
			throw new IllegalArgumentException("Invalid annotation");
		}
		else {
			this.tree = tree;
			this.annotation = annotation;
			this.in_nodes = new ArrayList<CirAnnotationNode>();
			this.ou_nodes = new ArrayList<CirAnnotationNode>();
		}
	}
	
	/* getters */
	/**
	 * @return the tree space where the node of CirAnnotation is defined
	 */
	public CirAnnotationTree get_tree() { return this.tree; }
	/**
	 * @return the unique annotation that this node represents in tree
	 */
	public CirAnnotation get_annotation() { return this.annotation; }
	/**
	 * @return the set of nodes of which annotations subsume this one
	 */
	public Iterable<CirAnnotationNode> get_in_nodes() { return this.in_nodes; }
	/**
	 * @return the set of nodes of which annotations subsumed by this
	 */
	public Iterable<CirAnnotationNode> get_ou_nodes() { return this.ou_nodes; }
	/**
	 * @return the set of all existing nodes subsumed by this one in the tree
	 */
	public Iterable<CirAnnotationNode> get_children() {
		Queue<CirAnnotationNode> queue = new LinkedList<CirAnnotationNode>();
		Set<CirAnnotationNode> records = new HashSet<CirAnnotationNode>();
		queue.add(this); records.add(this);
		while(!queue.isEmpty()) {
			CirAnnotationNode node = queue.poll();
			for(CirAnnotationNode ou_node : node.get_ou_nodes()) {
				if(!records.contains(ou_node)) {
					queue.add(ou_node);
					records.add(ou_node);
				}
			}
		}
		return records;
	}
	/**
	 * @param target_annotation
	 * @return It connects this node to the node of the target annotation
	 * @throws Exception
	 */
	protected CirAnnotationNode subsume(CirAnnotation target_annotation) throws Exception {
		if(target_annotation == null) {
			throw new IllegalArgumentException("Invalid target_annotation: null");
		}
		else {
			/* 1. return if the target_annotation exists in its children */
			for(CirAnnotationNode child : this.get_children()) {
				if(child.get_annotation().equals(target_annotation)) {
					return child;
				}
			}
			
			/* 2. create a new child w.r.t. the target annotation under */
			CirAnnotationNode target = this.tree.new_node(target_annotation);
			this.ou_nodes.add(target); target.in_nodes.add(this); return target;
		}
	}
	
}
