package com.jcsa.jcmutest.mutant.cir2mutant.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymInstance;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * It maintains the abstract extension tree from program entry to the mutated points and propagation nodes
 * annotated with instances that describe necessary conditions required for killing a mutant.
 * 
 * @author yukimula
 *
 */
public class SymInstanceTreeNode {
	
	/* definitions */
	/** the tree where this node is created **/
	private SymInstanceTree tree;
	/** the parent of this node or null if it is root **/
	private SymInstanceTreeNode parent;
	/** the children created under this node **/
	private List<SymInstanceTreeNode> children;
	/** the state of SymConstraint annotated in the input edge to this node **/
	private SymInstanceState edge_state;
	/** the state of SymStateError annotated in the local node for analysis **/
	private SymInstanceState node_state;
	
	/* constructors */
	/**
	 * @param tree
	 * @param node_instance
	 * @throws IllegalArgumentException
	 */
	protected SymInstanceTreeNode(SymInstanceTree tree, SymInstance node_instance) throws IllegalArgumentException {
		if(tree == null)
			throw new IllegalArgumentException("Invalid tree as null");
		else if(node_instance == null)
			throw new IllegalArgumentException("Invalid node_instance");
		else {
			this.tree = tree;
			this.parent = null;
			this.children = new LinkedList<SymInstanceTreeNode>();
			this.edge_state = null;
			this.node_state = new SymInstanceState(node_instance);
		}
	}
	/**
	 * create a child node from parent by adding edge and node instance as recorded
	 * @param parent
	 * @param edge_instance
	 * @param noed_instance
	 * @throws IllegalArgumentException
	 */
	private SymInstanceTreeNode(SymInstanceTreeNode parent, SymInstance edge_instance, 
			SymInstance node_instance) throws IllegalArgumentException {
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
			this.edge_state = new SymInstanceState(edge_instance);
			this.node_state = new SymInstanceState(node_instance);
		}
	}
	/**
	 * @param edge_instance
	 * @param node_instance
	 * @return create a new child tree node under the parent w.r.t. edge and node instance states
	 * @throws Exception
	 */
	protected SymInstanceTreeNode new_child(SymInstance edge_instance, SymInstance node_instance) throws Exception {
		SymInstanceTreeNode child = new SymInstanceTreeNode(this, edge_instance, node_instance);
		this.children.add(child);
		return child;
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
	 * @return the state of symbolic instance annotated on the input edge or null if the node is root
	 */
	public SymInstanceState get_edge_state() { return this.edge_state; }
	/**
	 * @return the state of symbolic instance annotated at the local node (usually state error or the
	 * 		   symbolic constraint for coverage node before mutated location).
	 */
	public SymInstanceState get_node_state() { return this.node_state; }
	/**
	 * @return the execution of statement node in control flow graph, where the tree node was defined.
	 */
	public CirExecution get_execution() { return this.node_state.get_execution(); }
	
	/* inferring */
	/**
	 * collect all the nodes under the node
	 * @param node
	 * @param nodes
	 */
	private void collect_children(SymInstanceTreeNode node, Collection<SymInstanceTreeNode> nodes) {
		nodes.add(node);
		for(SymInstanceTreeNode child : node.children) {
			this.collect_children(child, nodes);
		}
	}
	/**
	 * @return all the nodes under the node
	 */
	public Collection<SymInstanceTreeNode> get_sub_tree() {
		Set<SymInstanceTreeNode> nodes = new HashSet<SymInstanceTreeNode>();
		this.collect_children(this, nodes);
		return nodes;
	}
	/**
	 * @return path from root to this one
	 */
	public List<SymInstanceTreeNode> path_to_this() {
		List<SymInstanceTreeNode> path = new ArrayList<SymInstanceTreeNode>();
		
		SymInstanceTreeNode node = this;
		while(node != null) {
			path.add(node);
			node = node.parent;
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
	 * @return whether the node is acceptable (reached ever)
	 */
	public boolean is_acceptable() { return this.node_state.is_acceptable(); } 
	
}
