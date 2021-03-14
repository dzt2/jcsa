package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymInstance;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;


/**
 * The node in symbolic instance tree for evaluating, which maintains the abstract extension tree from program entry 
 * to the mutated points and propagation nodes annotated with instances that describe necessary conditions required 
 * for killing a mutation.
 * 
 * @author yukimula
 *
 */
public class SymInstanceTreeNode {
	
	/* definitions */
	/** the tree where this node is created **/
	private SymInstanceTree				tree;
	/** the parent of this node or null if it is root **/
	private SymInstanceTreeNode			parent;
	/** the children created under this node **/
	private List<SymInstanceTreeNode> 	children;
	/** the states of instance annotated in the input edge to this node **/
	private SymInstanceStatus 			edge_status;
	/** the states of instance annotated in the local node of this ones **/
	private SymInstanceStatus			node_status;
	
	/* constructors */
	/**
	 * create the root node in tree w.r.t. given instance in node status (of
	 * which edge state is null).
	 * 
	 * @param tree
	 * @param instance
	 * @throws Exception
	 */
	protected SymInstanceTreeNode(SymInstanceTree tree, SymInstance instance) throws Exception {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree as null");
		else if(instance == null)
			throw new IllegalArgumentException("Invalid instance.");
		else {
			this.tree = tree;
			this.parent = null;
			this.children = new LinkedList<SymInstanceTreeNode>();
			this.edge_status = null;
			this.node_status = new SymInstanceStatus(instance);
		}
	}
	/**
	 * create the child node under the parent w.r.t. instances of edge status and node status.
	 * @param parent
	 * @param edge_instance
	 * @param node_instance
	 * @throws Exception
	 */
	private SymInstanceTreeNode(SymInstanceTreeNode parent, SymInstance 
			edge_instance, SymInstance node_instance) throws Exception {
		if(parent == null)
			throw new IllegalArgumentException("Invalid parent: null");
		else if(edge_instance == null)
			throw new IllegalArgumentException("Invalid edge_instance");
		else if(node_instance == null)
			throw new IllegalArgumentException("Invalid node_instance");
		else {
			this.tree = parent.tree;
			this.parent = parent;
			this.children = new LinkedList<SymInstanceTreeNode>();
			this.edge_status = new SymInstanceStatus(edge_instance);
			this.node_status = new SymInstanceStatus(node_instance);
		}
	}
	/**
	 * @param edge_instance
	 * @param node_instance
	 * @return create a new child tree node under the parent w.r.t. edge and node instance states
	 * @throws Exception
	 */
	protected SymInstanceTreeNode new_child(SymInstance edge_instance, SymInstance node_instance) throws Exception {
		SymInstanceTreeNode child = new SymInstanceTreeNode(
						this, edge_instance, node_instance);
		this.children.add(child);	return child;
	}
	
	/* getters */
	/**
	 * @return the tree where this node is created
	 */
	public SymInstanceTree get_tree() { return this.tree; }
	/**
	 * @return the parent of this node or null if it is root
	 */
	public SymInstanceTreeNode get_parent() { return this.parent; }
	/**
	 * @return whether the node is a root in tree
	 */
	public boolean is_root() { return this.parent == null; }
	/**
	 * @return the children created under this tree node
	 */
	public Iterable<SymInstanceTreeNode> get_children() { return this.children; }
	/**
	 * @return whether the node is a leaf without any child
	 */
	public boolean is_leaf() { return this.children.isEmpty(); }
	/**
	 * @return the number of children created under the node
	 */
	public int number_of_children() { return this.children.size(); }
	/**
	 * @param k
	 * @return the kth child under the node
	 * @throws IndexOutOfBoundsException
	 */
	public SymInstanceTreeNode get_child(int k) throws IndexOutOfBoundsException {
		return this.children.get(k);
	}
	/**
	 * @return the states of symbolic instance annotated on the input edge or null if the node is root
	 */
	public SymInstanceStatus get_edge_status() { return this.edge_status; }
	/**
	 * @return the states of symbolic instance annotated at the local node (usually state error or the
	 * 		   symbolic constraint for coverage node before mutated location).
	 */
	public SymInstanceStatus get_node_status() { return this.node_status; }
	/**
	 * @return whether the edge_status exists (when node is not the root).
	 */
	public boolean has_edge_status() { return this.edge_status != null; }
	/**
	 * @return the execution of statement node in control flow graph, where the tree node was defined.
	 */
	public CirExecution get_execution() { return this.node_status.get_execution(); }
	
	/* implication */
	/**
	 * collect the children under this node recursively
	 * @param children set to preserve nodes under this
	 */
	private void collect_children(Collection<SymInstanceTreeNode> children) {
		children.add(this);
		for(SymInstanceTreeNode child : this.children) {
			child.collect_children(children);
		}
	}
	/** 
	 * @return the collection of all children under this node (including itself)
	 */
	protected Collection<SymInstanceTreeNode> get_all_children() {
		Set<SymInstanceTreeNode> children = new HashSet<SymInstanceTreeNode>();
		this.collect_children(children);
		return children;
	}
	/**
	 * @return the sequence from root to this node in the tree
	 */
	protected List<SymInstanceTreeNode> get_parents_path() {
		List<SymInstanceTreeNode> path = new ArrayList<SymInstanceTreeNode>();
		
		SymInstanceTreeNode node = this;
		while(node != null) {
			path.add(node); node = node.parent;
		}
		
		for(int k = 0; k < path.size() / 2; k++) {
			SymInstanceTreeNode x = path.get(k);
			SymInstanceTreeNode y = path.get(path.size() - 1 - k);
			path.set(k, y);
			path.set(path.size() - 1 - k, x);
		}
		
		return path;
	}
	/**
	 * @return the tree node is satisfiable when its node status is satisfiable.
	 */
	protected boolean is_acceptable() {
		return this.node_status.is_acceptable();
	}
	
}
