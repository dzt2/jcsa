package com.jcsa.jcmutest.mutant.cir2mutant.graph;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutation;

/**
 * Each node in error propagation tree in local statement is referred to one
 * cir-mutation with constraint and state error, which are required for being
 * satisfied such that the mutation under analysis will be killed.
 * 
 * @author yukimula
 *
 */
public class CirMutationTreeNode {
	
	/* definitions */
	/** the error propagation tree where the node is created **/
	private CirMutationTree tree;
	/** the mutation that this node is referred to **/
	private CirMutation cir_mutation;
	/** the parent of this node or null if the node is a root **/
	private CirMutationTreeNode parent;
	/** the children under which the tree node are created **/
	private List<CirMutationTreeNode> children;
	
	/* constructor */
	/**
	 * create a root node in the tree w.r.t. the mutation
	 * @param tree
	 * @param mutation
	 * @throws Exception
	 */
	protected CirMutationTreeNode(CirMutationTree tree, CirMutation mutation) throws Exception {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation as null");
		else {
			this.tree = tree;
			this.cir_mutation = mutation;
			this.parent = null;
			this.children = new LinkedList<CirMutationTreeNode>();
		}
	}
	/**
	 * create a child node under the parent w.r.t. the mutation as specified
	 * @param parent
	 * @param mutation
	 * @throws Exception
	 */
	private CirMutationTreeNode(CirMutationTreeNode parent, CirMutation mutation) throws Exception {
		if(parent == null)
			throw new IllegalArgumentException("Invalid parent: null");
		else if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation as null");
		else {
			this.tree = parent.tree;
			this.cir_mutation = mutation;
			this.parent = parent;
			this.children = new LinkedList<CirMutationTreeNode>();
		}
	}
	
	/* getters */
	/**
	 * @return the tree in which the node is created
	 */
	public CirMutationTree get_tree() { return this.tree; }
	/**
	 * @return the cir-mutation to which the tree node is referred
	 */
	public CirMutation get_cir_mutation() { return this.cir_mutation; }
	/**
	 * @return whether the node is a root in its tree
	 */
	public boolean is_root() { return this.parent == null; }
	/**
	 * @return the parent of this node or null if it is the root of the tree
	 */
	public CirMutationTreeNode get_parent() { return this.parent; }
	/**
	 * @return whether the node is one of the leafs in the tree
	 */
	public boolean is_leaf() { return this.children.isEmpty(); }
	/**
	 * @return the children created under this tree node
	 */
	public Iterable<CirMutationTreeNode> get_children() { return this.children; }
	
	/* setters */
	/**
	 * @param mutation
	 * @return the child under this parent w.r.t. the mutation
	 * @throws Exception
	 */
	protected CirMutationTreeNode new_child(CirMutation mutation) throws Exception {
		/* 1. try to find the existing child w.r.t. the mutation */
		for(CirMutationTreeNode child : this.children) {
			if(child.cir_mutation == mutation) {
				return child;
			}
		}
		
		/* 2. create a new child w.r.t. the mutation under the node */
		CirMutationTreeNode child = new CirMutationTreeNode(this, mutation);
		this.children.add(child);
		return child;
	}
	/**
	 * delete this node from the tree
	 */
	protected void delete() {
		if(this.cir_mutation != null) {
			this.tree = null;
			this.cir_mutation = null;
			this.parent = null;
			this.children.clear();
			this.children = null;
		}
	}
	
}
