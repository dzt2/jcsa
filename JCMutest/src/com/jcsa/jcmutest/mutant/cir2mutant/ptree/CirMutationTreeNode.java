package com.jcsa.jcmutest.mutant.cir2mutant.ptree;

import java.util.LinkedList;
import java.util.List;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutation;
import com.jcsa.jcparse.test.state.CStateContexts;

/**
 * The node in mutation propagation tree represents a state error
 * as well as the constraint for causing it.
 * 
 * @author yukimula
 *
 */
public class CirMutationTreeNode {
	
	/* definitions */
	/** the tree where this node is created and belongs to **/
	private CirMutationTree tree;
	/** the mutation that the node represents **/
	private CirMutation cir_mutation;
	/** the parent node of this node or null if it's root **/
	private CirMutationTreeNode parent;
	/** the children created under this node as their parent **/
	private List<CirMutationTreeNode> children;
	/** the index of the node as child of its parent **/
	private int child_index;
	/** the type of the propagation flow from parent to this child **/
	private CirMutationFlowType flow_type;
	
	/* constructors */
	/**
	 * create a virtual node as the root of the tree w.r.t. the mutation as given
	 * @param tree
	 * @param cir_mutation
	 * @throws Exception
	 */
	protected CirMutationTreeNode(CirMutationTree tree, CirMutation cir_mutation) throws IllegalArgumentException {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree: null");
		else if(cir_mutation == null)
			throw new IllegalArgumentException("Invalid cir_mutation");
		else {
			this.tree = tree;
			this.cir_mutation = cir_mutation;
			this.parent = null;
			this.children = new LinkedList<CirMutationTreeNode>();
			this.child_index = -1;
			this.flow_type = null;
		}
	}
	/**
	 * create a child node under the parent w.r.t. the mutation
	 * @param parent
	 * @param cir_mutation
	 * @throws IllegalArgumentException
	 */
	private CirMutationTreeNode(CirMutationTreeNode parent, CirMutation cir_mutation, 
			CirMutationFlowType flow_type) throws IllegalArgumentException {
		if(parent == null)
			throw new IllegalArgumentException("No parent specified");
		else if(cir_mutation == null)
			throw new IllegalArgumentException("Invalid cir_mutation");
		else if(flow_type == null)
			throw new IllegalArgumentException("Invalid flow_type");
		else {
			this.tree = parent.tree;
			this.cir_mutation = cir_mutation;
			this.parent = parent;
			this.children = new LinkedList<CirMutationTreeNode>();
			this.child_index = parent.children.size();
			this.flow_type = flow_type;
		}
	}
	
	/* getters */
	/**
	 * @return the tree where this node is created and belongs to
	 */
	public CirMutationTree get_tree() { return this.tree; }
	/**
	 * @return the mutation that the node represents
	 */
	public CirMutation get_cir_mutation() { return this.cir_mutation; }
	/**
	 * @return the parent node of this node or null if it's root
	 */
	public CirMutationTreeNode get_parent() { return this.parent; }
	/**
	 * @return the children created under this node as their parent
	 */
	public Iterable<CirMutationTreeNode> get_children() { return this.children; }
	/**
	 * @return the number of children under this node
	 */
	public int number_of_children() { return this.children.size(); }
	/**
	 * @param k
	 * @return the kth child under this node
	 * @throws IndexOutOfBoundsException
	 */
	public CirMutationTreeNode get_child(int k) throws IndexOutOfBoundsException {
		return this.children.get(k);
	}
	/**
	 * @return the index of the node as child of its parent (or -1 for root)
	 */
	public int get_child_index() { return this.child_index; }
	/**
	 * @return the type of the propagation flow from parent to this child (or null for root)
	 */
	public CirMutationFlowType get_flow_type() { return this.flow_type; }
	
	/* inference */
	/**
	 * @return whether the node is the root of the tree
	 */
	public boolean is_root() { return this.parent == null; }
	/**
	 * @return whether the node is a leaf in the tree
	 */
	public boolean is_leaf() { return this.children.isEmpty(); }
	/**
	 * @return the root of the tree of the node
	 */
	public CirMutationTreeNode get_root() {
		CirMutationTreeNode root = this;
		while(!root.is_root()) {
			root = root.parent;
		}
		return root;
	}
	/**
	 * @param contexts
	 * @return the mutation optimized under the given contexts
	 * @throws Exception
	 */
	public CirMutation optimize_by(CStateContexts contexts) throws Exception {
		return this.tree.get_trees().get_cir_mutations().
					optimize(this.cir_mutation, contexts);
	}
	
	/* setters */
	/**
	 * @param flow_type
	 * @param cir_mutation
	 * @return link this node to its child w.r.t. the given mutation and specified flow-type
	 * @throws IllegalArgumentException
	 */
	protected CirMutationTreeNode new_child(CirMutationFlowType flow_type, CirMutation cir_mutation) throws IllegalArgumentException {
		if(cir_mutation == null)
			throw new IllegalArgumentException("Invalid cir_mutation");
		else {
			for(CirMutationTreeNode child : this.children) {
				if(child.cir_mutation.equals(cir_mutation)) {
					if(child.flow_type == flow_type)
						return child;
				}
			}
			CirMutationTreeNode child = new CirMutationTreeNode(this, cir_mutation, flow_type);
			this.children.add(child);
			return child;
		}
	}
	/**
	 * remove this node from the tree
	 */
	protected void delete() {
		this.tree = null;
		this.cir_mutation = null;
		this.child_index = -1;
		this.flow_type = null;
		if(this.children != null) {
			for(CirMutationTreeNode child : this.children) {
				child.delete();
			}
			this.children.clear();
		}
		this.parent = null;
		this.children = null;
	}
	
}
