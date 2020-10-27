package com.jcsa.jcmutest.mutant.cir2mutant.graph;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutation;

/**
 * The error propagation tree in local statement.
 * 
 * @author yukimula
 *
 */
public class CirMutationTree {
	
	/* definitions */
	/** the node in which the tree is created **/
	private CirMutationNode node;
	/** the root node of this tree **/
	private CirMutationTreeNode root;
	/** the leafs under the tree  **/
	private List<CirMutationTreeNode> leafs;
	
	/* constructor */
	/**
	 * create the tree with a unique root node w.r.t. the mutation
	 * @param node
	 * @param mutation
	 * @throws Exception
	 */
	protected CirMutationTree(CirMutationNode node, CirMutation mutation) throws Exception {
		if(node == null)
			throw new IllegalArgumentException("Invalid node: null");
		else if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation as null");
		else {
			this.node = node;
			this.root = new CirMutationTreeNode(this, mutation);
			this.leafs = new LinkedList<CirMutationTreeNode>();
		}
	}
	
	/* getters */
	/**
	 * @return the statement node where the tree is created
	 */
	public CirMutationNode get_statement_node() { return this.node; }
	/**
	 * @return the root node under the error propagation tree
	 */
	public CirMutationTreeNode get_root() { return this.root; }
	/**
	 * @return the leafs under the tree nodes in the tree
	 */
	public Iterable<CirMutationTreeNode> get_leafs() { return this.leafs; } 
	
	/* setters */
	/**
	 * update the leafs under the tree
	 * @throws Exception
	 */
	protected void update_leafs() throws Exception {
		Queue<CirMutationTreeNode> queue = new LinkedList<CirMutationTreeNode>();
		this.leafs.clear();
		queue.add(this.root);
		while(!queue.isEmpty()) {
			CirMutationTreeNode tree_node = queue.poll();
			for(CirMutationTreeNode child : tree_node.get_children()) {
				queue.add(child);
			}
			this.leafs.add(tree_node);
		}
	}
	/**
	 * clear the nodes within the tree node
	 */
	protected void clear_tree() throws Exception {
		CirMutation root_mutation = this.root.get_cir_mutation();
		
		Queue<CirMutationTreeNode> queue = new LinkedList<CirMutationTreeNode>();
		queue.add(this.root);
		while(!queue.isEmpty()) {
			CirMutationTreeNode tree_node = queue.poll();
			for(CirMutationTreeNode child : tree_node.get_children()) {
				queue.add(child);
			}
			tree_node.delete();
		}
		
		this.root = new CirMutationTreeNode(this, root_mutation);
		this.leafs.clear();
	}
	
}
