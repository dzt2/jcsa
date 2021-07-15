package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.base.SymCondition;

/**
 * Each node in cir-mutation tree.
 * 
 * @author yukimula
 *
 */
public class CirMutationNode {
	
	/* attributes */
	/** tree where the node is created **/
	private CirMutationTree tree;
	/** the condition this node represents **/
	private SymCondition condition;
	/** edge from parent to this node or null if it is root **/
	private CirMutationEdge in_edge;
	/** edges from this node to its direct children in tree **/
	private List<CirMutationEdge> ou_edges;
	
	/* constructor */
	/**
	 * @param tree
	 * @param condition
	 * @throws IllegalArgumentException
	 */
	protected CirMutationNode(CirMutationTree tree, SymCondition condition) throws IllegalArgumentException {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree as null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			this.tree = tree;
			this.condition = condition;
			this.in_edge = null;
			this.ou_edges = new LinkedList<CirMutationEdge>();
		}
	}
	/**
	 * create a node under the parent linked with specified flow type and symbolic condition
	 * @param parent
	 * @param flow
	 * @param condition
	 * @throws IllegalArgumentException
	 */
	private CirMutationNode(CirMutationNode parent, CirMutationFlow flow, SymCondition condition) throws IllegalArgumentException {
		if(parent == null) {
			throw new IllegalArgumentException("Invalid tree as null");
		}
		else if(flow == null) {
			throw new IllegalArgumentException("Invalid flow as null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			this.tree = parent.tree;
			this.condition = condition;
			this.in_edge = new CirMutationEdge(flow, parent, this);
			parent.ou_edges.add(this.in_edge);
			this.ou_edges = new LinkedList<CirMutationEdge>();
		}
	}
	
	/* getters */
	/**
	 * @return tree where the node is created
	 */
	public CirMutationTree get_tree() { return this.tree; }
	/**
	 * @return the condition this node represents
	 */
	public SymCondition get_condition() { return this.condition; }
	/**
	 * @return edge from parent to this node or null if it is root
	 */
	public CirMutationEdge get_in_edge() { return this.in_edge; }
	/**
	 * @return edges from this node to its direct children in tree
	 */
	public Iterable<CirMutationEdge> get_ou_edges() { return this.ou_edges; }
	/**
	 * @return whether the node is a root without parent
	 */
	public boolean is_root() { return this.in_edge == null; }
	/**
	 * @return whether the node is a leaf without children
	 */
	public boolean is_leaf() { return this.ou_edges.isEmpty(); }
	/**
	 * @return the number of children under this node
	 */
	public int number_of_children() { return this.ou_edges.size(); }
	/**
	 * @param k
	 * @return the kth parent-child edge under this node
	 * @throws IndexOutOfBoundsException
	 */
	public CirMutationEdge get_ou_edge(int k) throws IndexOutOfBoundsException {
		return this.ou_edges.get(k);
	}
	@Override
	public String toString() { return this.condition.toString(); }
	
	/* setters */
	/**
	 * delete this node from tree space
	 */
	protected void delete() { 
		if(this.tree != null) {
			this.tree = null;
			this.condition = null;
			this.in_edge = null;
			this.ou_edges.clear();
			this.ou_edges = null;
		}
	}
	/**
	 * @param flow
	 * @param condition
	 * @return itself if the condition matches or a child (existing or created)
	 * @throws IllegalArgumentException
	 */
	protected CirMutationNode add_child(CirMutationFlow flow, SymCondition condition) throws IllegalArgumentException {
		if(flow == null) {
			throw new IllegalArgumentException("Invalid flow as null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else if(this.condition.equals(condition)) {
			return this;
		}
		else {
			for(CirMutationEdge ou_edge : this.ou_edges) {
				if(ou_edge.get_target().get_condition().equals(condition)) {
					return ou_edge.get_target();
				}
			}
			return new CirMutationNode(this, flow, condition);
		}
	}
	
}
