package com.jcsa.jcmutest.mutant.cir2mutant.rtree;

import java.util.LinkedList;
import java.util.List;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutation;

/**
 * Each node in error-propagation tree refers to a CirMutation.
 * 
 * @author yukimula
 *
 */
public class CirMutationTreeNode {
	
	/* definitions */
	/** the tree where this node is created **/
	private CirMutationTree tree;
	/** cir-mutation that defines this node in the graph **/
	private CirMutation mutation;
	/** the parent of this tree node or null if it is root **/
	private CirMutationTreeNode parent;
	/** the children created under this tree node **/
	private List<CirMutationTreeNode> children;
	
	/* constructor */
	/**
	 * create a root node under the tree w.r.t. the mutation
	 * @param tree
	 * @param mutation
	 * @throws Exception
	 */
	protected CirMutationTreeNode(CirMutationTree tree, CirMutation mutation) throws Exception {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else {
			this.tree = tree;
			this.mutation = mutation;
			this.parent = null;
			this.children = new LinkedList<CirMutationTreeNode>();
		}
	}
	/**
	 * create a tree node as the child of specified parent and mutation
	 * @param parent
	 * @param mutation
	 * @throws Exception
	 */
	private CirMutationTreeNode(CirMutationTreeNode parent, CirMutation mutation) throws Exception {
		if(parent == null)
			throw new IllegalArgumentException("invalid parent: null");
		else if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else if(mutation.get_statement() != parent.mutation.get_statement())
			throw new IllegalArgumentException("Unmatched statements");
		else {
			this.tree = parent.tree;
			this.mutation = mutation;
			this.parent = parent;
			this.children = new LinkedList<CirMutationTreeNode>();
		}
	}
	
	/* getters */
	/**
	 * @return the tree where this node is created
	 */
	public CirMutationTree get_tree() { return this.tree; }
	/**
	 * @return cir-mutation that defines this node in the graph
	 */
	public CirMutation get_mutation() { return this.mutation; }
	/**
	 * @return the parent of this tree node or null if it is root
	 */
	public CirMutationTreeNode get_parent() { return this.parent; }
	/**
	 * @return the children created under this tree node
	 */
	public Iterable<CirMutationTreeNode> get_children() { return this.children; }
	public boolean is_root() { return this.parent == null; }
	public boolean is_leaf() { return this.children.size() == 0; }
	
	/* setter */
	/**
	 * @param mutation
	 * @return get or create the child node under this node w.r.t. the mutation
	 * @throws Exception
	 */
	protected CirMutationTreeNode get_child(CirMutation mutation) throws Exception {
		if(mutation == null)
			throw new IllegalArgumentException("Invalid mutation: null");
		else {
			for(CirMutationTreeNode child : this.children) {
				if(child.mutation == mutation)
					return child;
			}
			CirMutationTreeNode child = new CirMutationTreeNode(this, mutation);
			this.children.add(child);
			return child;
		}
	}
	
}
