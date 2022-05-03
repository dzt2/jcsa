package com.jcsa.jcmutest.mutant.ctx2mutant.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.ctx2mutant.base.AstContextState;

public class ContextMutationNode {
	
	/** the tree where this node is created **/
	private	ContextMutationTree			tree;
	/** the integer ID of this node in tree **/
	private	int							node_id;
	/** the state that is represented by this node **/
	private	AstContextState				state;
	/** the edges from this node to the next nodes **/
	private	List<ContextMutationEdge>	ou_edges;
	/** the edges from previous nodes to this one **/
	private	List<ContextMutationEdge>	in_edges;
	/** the set of annotations represent this node **/
	private Set<ContextAnnotation>		annotations;
	
	/**
	 * @param tree	the tree where this node is created
	 * @param id	the integer ID of this node in tree
	 * @param state	the state that is represented by this node
	 * @throws IllegalArgumentException
	 */
	protected	ContextMutationNode(ContextMutationTree tree, 
			int id, AstContextState state) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else if(state == null) {
			throw new IllegalArgumentException("Invalid state: null");
		}
		else {
			this.tree = tree; this.node_id = id; this.state = state;
			this.in_edges = new ArrayList<ContextMutationEdge>();
			this.ou_edges = new ArrayList<ContextMutationEdge>();
			this.annotations = new HashSet<ContextAnnotation>();
			ContextAnnotationUtils.extend(this.state, this.annotations);
		}
	}
	
	/**
	 * @return the tree where this node is created
	 */
	public ContextMutationTree	get_tree() { return this.tree; }
	/**
	 * @return the integer ID of this node in tree
	 */
	public int					get_node_id() { return this.node_id; }
	/**
	 * @return the state that is represented by this node
	 */
	public AstContextState		get_state() { return this.state; }
	/**
	 * @return the edges from previous nodes to this node
	 */
	public Iterable<ContextMutationEdge> get_in_edges() { return this.in_edges; }
	/**
	 * @return the number of the edges from previous nodes to this node
	 */
	public int get_in_degree() { return this.in_edges.size(); }
	/**
	 * @param k
	 * @return the kth edge from previous nodes to this node
	 * @throws IndexOutOfBoundsException
	 */
	public ContextMutationEdge get_in_edge(int k) throws IndexOutOfBoundsException { return this.in_edges.get(k); }
	/**
	 * @return the edges from this node to the next nodes
	 */
	public Iterable<ContextMutationEdge> get_ou_edges() { return this.ou_edges; }
	/**
	 * @return the number of the edges from this node to the next nodes
	 */
	public int get_ou_degree() { return this.ou_edges.size(); }
	/**
	 * @param k
	 * @return the kth edge from this node to the next nodes
	 * @throws IndexOutOfBoundsException
	 */
	public ContextMutationEdge get_ou_edge(int k) throws IndexOutOfBoundsException { return this.ou_edges.get(k); }
	/**
	 * @return the set of annotations represent this node
	 */
	public Iterable<ContextAnnotation> get_annotations() { return this.annotations; }
	/**
	 * It connects this node to the target
	 * @param target
	 * @throws Exception
	 */
	protected ContextMutationEdge connect(ContextMutationNode target) throws Exception {
		if(target == null) {
			throw new IllegalArgumentException("Invalid target: null");
		}
		else if(this == target) { return null; }
		else {
			for(ContextMutationEdge edge : this.ou_edges) {
				if(edge.get_target() == target) return edge;
			}
			ContextMutationEdge edge = new ContextMutationEdge(this, target);
			this.ou_edges.add(edge); target.in_edges.add(edge); return edge;
		}
	}
	
}
