package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * It represents a unique node that denotes a particular CirAnnotation in tree.
 * 
 * @author yukimula
 *
 */
public class CirAnnotationNode {
	
	/* definitions */
	private CirAnnotationTree tree;
	private CirAnnotation annotation;
	private List<CirAnnotationNode> children;
	protected CirAnnotationNode(CirAnnotationTree tree, CirAnnotation annotation) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree as null");
		}
		else if(annotation == null) {
			throw new IllegalArgumentException("Invalid annotation: null");
		}
		else {
			this.tree = tree;
			this.annotation = annotation;
			this.children = new ArrayList<CirAnnotationNode>();
		}
	}
	
	/* getters */
	/**
	 * @return the tree where this node is created 
	 */
	public CirAnnotationTree get_tree() { return this.tree; }
	/**
	 * @return the annotation that the node represents
	 */ 
	public CirAnnotation get_annotation() { return this.annotation; }
	/**
	 * @return the annotation node that is directly subsumed by this one
	 */
	public Iterable<CirAnnotationNode> get_children() { return this.children; }
	/**
	 * @param annotation
	 * @return the child w.r.t. the unique annotation
	 * @throws Exception
	 */
	protected CirAnnotationNode new_child(CirAnnotation annotation) throws Exception {
		if(annotation == null) {
			throw new IllegalArgumentException("Invalid annotation as null");
		}
		else {
			Queue<CirAnnotationNode> queue = new LinkedList<CirAnnotationNode>();
			Set<CirAnnotationNode> records = new HashSet<CirAnnotationNode>();
			
			queue.add(this);
			while(!queue.isEmpty()) {
				CirAnnotationNode node = queue.poll();
				if(node.get_annotation().equals(annotation)) {
					return node;
				}
				else {
					records.add(node);
					for(CirAnnotationNode child : node.get_children()) {
						if(!records.contains(child)) {
							queue.add(child);
						}
					}
				}
			}
			
			CirAnnotationNode child = this.tree.new_node(annotation);
			this.children.add(child);
			return child;
		}
	}
	
}
