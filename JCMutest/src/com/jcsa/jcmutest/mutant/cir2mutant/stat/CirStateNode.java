package com.jcsa.jcmutest.mutant.cir2mutant.stat;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * A node in execution state tree.
 * 
 * @author yukimula
 *
 */
public class CirStateNode {
	
	/* definitions */
	private CirStateTree tree;
	private CirStateType type;
	private CirStateNode parent;
	private List<CirStateNode> children;
	private CirStateData data;
	
	/* constructor */
	protected CirStateNode(CirStateTree tree, CirExecution execution) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree as null");
		}
		else if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			this.tree = tree;
			this.type = CirStateType.pre;
			this.parent = null;
			this.children = new ArrayList<CirStateNode>();
			this.data = new CirStateData(execution);
		}
	}
	private CirStateNode(CirStateNode parent, CirStateType type, CirExecution execution) throws Exception {
		if(parent == null) {
			throw new IllegalArgumentException("Invalid parent as null");
		}
		else if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(execution == null) {
			throw new IllegalArgumentException("Invalid execution: null");
		}
		else {
			this.tree = parent.tree;
			this.type = type;
			this.parent = parent;
			this.children = new ArrayList<CirStateNode>();
			this.data = new CirStateData(execution);
		}
	}
	protected CirStateNode new_child(CirStateType type, CirExecution execution) throws Exception {
		CirStateNode child = new CirStateNode(this, type, execution);
		this.children.add(child);
		return child;
	}
	
	/* getters */
	/**
	 * @return the tree where the node is defined
	 */
	public CirStateTree get_tree() { return this.tree; }
	/**
	 * @return the type of node w.r.t. RIP process
	 */
	public CirStateType get_type() { return this.type; }
	/**
	 * @return data state hold in this node
	 */
	public CirStateData get_data() { return this.data; }
	/**
	 * @return where this node is evaluated
	 */
	public CirExecution get_execution() { return this.data.get_execution(); }
	/**
	 * @return the parent where this node is created or null if it is root
	 */
	public CirStateNode get_parent() { return this.parent; }
	/**
	 * @return the children created under this node
	 */
	public Iterable<CirStateNode> get_children() { return this.children; }
	/**
	 * @return the number of children created under this node
	 */
	public int number_of_children() { return this.children.size(); }
	/**
	 * @param k
	 * @return the kth child under this node
	 * @throws IndexOutOfBoundsException
	 */
	public CirStateNode get_child(int k) throws IndexOutOfBoundsException { return this.children.get(k); }
	/**
	 * @return whether the node is a root without parent
	 */
	public boolean is_root() { return this.parent == null; }
	/**
	 * @return whether the node is a leaf without children
	 */
	public boolean is_leaf() { return this.children.isEmpty(); }
	@Override
	public String toString() { return this.data.toString(); }
	
}
