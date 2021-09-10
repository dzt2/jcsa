package com.jcsa.jcmutest.mutant.cir2mutant.path;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * It represents an execution point in execution state tree.
 * 
 * @author yukimula
 *
 */
public class CirStateNode {
	
	/* definitions */
	/** the execution state tree where the node is created **/
	private CirStateTree tree;
	/** the type denoting at which step the node belong to **/
	private CirStateType type;
	/** the data of store-value hold within the state node **/
	private CirStateData data;
	/** the parent node of this state node or null if root **/
	private CirStateNode parent;
	/** the children created under the execution state node **/
	private List<CirStateNode> children;
	
	/* constructors */
	/**
	 * It creates the root of the execution state tree, representing the coverage
	 * of program entry (input execution node)
	 * @param tree
	 * @param execution
	 * @throws Exception
	 */
	protected CirStateNode(CirStateTree tree, CirExecution execution) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree: null");
		}
		else if(execution == null) {
			throw new IllegalArgumentException("Invalid execution;");
		}
		else {
			this.tree = tree;
			this.type = CirStateType.pre_condition;
			this.data = new CirStateData(execution);
			this.parent = null;
			this.children = new ArrayList<CirStateNode>();
		}
	}
	/**
	 * It creates the child node under the parent using the given node_type and specified execution of state
	 * @param parent
	 * @param node_type
	 * @param execution
	 * @throws Exception
	 */
	private CirStateNode(CirStateNode parent, CirStateType node_type, CirExecution execution) throws Exception {
		if(parent == null) {
			throw new IllegalArgumentException("Invalid parent as null.");
		}
		else if(node_type == null) {
			throw new IllegalArgumentException("Invalid node_type: null");
		}
		else if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			this.tree = parent.tree;
			this.type = node_type;
			this.data = new CirStateData(execution);
			this.parent = parent;
			this.children = new ArrayList<CirStateNode>();
		}
	}
	/**
	 * It creates a new child from this parent and update it into the children
	 * @param node_type
	 * @param execution
	 * @return
	 * @throws Exception
	 */
	protected CirStateNode new_child(CirStateType node_type, CirExecution execution) throws Exception {
		if(node_type == null) {
			throw new IllegalArgumentException("Invalid node_type: null");
		}
		else if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			CirStateNode child = new CirStateNode(this, node_type, execution);
			this.children.add(child);
			return child;
		}
	}
	
	/* getters */
	/**
	 * @return the execution state tree where the node is created
	 */
	public CirStateTree 			get_tree() 		{ return this.tree; }
	/**
	 * @return the type denoting at which step the node belong to
	 */
	public CirStateType 			get_type() 		{ return this.type; }
	/**
	 * @return the data of store-value hold within the state node
	 */
	public CirStateData 			get_data() 		{ return this.data; }
	/**
	 * @return the execution point where this state is defined
	 */
	public CirExecution				get_execution() { return this.data.get_execution(); }
	/**
	 * @return the parent node of this state node or null if root 
	 */
	public CirStateNode 			get_parent()	{ return this.parent; }
	/**
	 * @return the children created under the execution state node
	 */
	public Iterable<CirStateNode>	get_children()	{ return this.children; }
	/**
	 * @return whether this state node is a root without parent
	 */
	public boolean is_root() { return this.parent == null; }
	/**
	 * @return whether this state node is a leaf without children
	 */
	public boolean is_leaf() { return this.children.isEmpty(); }
	/**
	 * @return the number of children nodes created under the one
	 */
	public int number_of_children() { return this.children.size(); }
	/**
	 * @param k
	 * @return the kth child state created under this one
	 * @throws IndexOutOfBoundsException
	 */
	public CirStateNode get_child(int k) throws IndexOutOfBoundsException {
		return this.children.get(k);
	}
	
	/* iterator */
	/**
	 * It iterates the nodes under the root using BFS-traversal from the root.
	 * @author yukimula
	 *
	 */
	private static class CirStateNodeIterator implements Iterator<CirStateNode> {
		private Queue<CirStateNode> queue;
		
		private CirStateNodeIterator(CirStateNode root) {
			queue = new LinkedList<CirStateNode>();
			this.queue.add(root);
		}

		@Override
		public boolean hasNext() {
			return !this.queue.isEmpty();
		}

		@Override
		public CirStateNode next() {
			CirStateNode node = this.queue.poll();
			for(CirStateNode child : node.get_children()) {
				this.queue.add(child);
			}
			return node;
		}
	}
	/**
	 * @return the iterator that traverses the node and its children using BFS.
	 */
	public Iterator<CirStateNode> get_post_nodes() {
		return new CirStateNodeIterator(this);
	}
	/**
	 * @return the list from root until this node
	 */
	public Iterable<CirStateNode> get_pred_nodes() {
		List<CirStateNode> nodes = new ArrayList<CirStateNode>();
		
		CirStateNode node = this;
		while(node != null) {
			nodes.add(node);
			node = node.get_parent();
		}
		
		for(int k = 0; k < nodes.size(); k++) {
			int i = k, j = nodes.size() - 1 - k;
			CirStateNode ni = nodes.get(i);
			CirStateNode nj = nodes.get(j);
			nodes.set(i, nj); nodes.set(j, ni);
		}
		
		return nodes;
	}
	
}
