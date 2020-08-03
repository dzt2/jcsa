package com.jcsa.jcparse.lang.ir.impl;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.unit.CirFunctionDefinition;

public abstract class CirNodeImpl implements CirNode {
	
	private CirTree tree;
	private int node_id;
	private CirNode parent;
	private int child_index;
	private List<CirNode> children;
	
	/**
	 * create an isolated node in the tree
	 * @param tree
	 */
	protected CirNodeImpl(CirTree tree) {
		this.tree = tree;
		this.node_id = -1;
		this.parent = null;
		this.child_index = -1;
		this.children = new LinkedList<CirNode>();
	}
	

	@Override
	public CirTree get_tree() { return this.tree; }

	@Override
	public int get_node_id() { return this.node_id; }

	@Override
	public void set_node_id(int key) { this.node_id = key; }

	@Override
	public CirNode get_parent() { return this.parent; }

	@Override
	public int get_child_index() { return this.child_index; }

	@Override
	public Iterable<CirNode> get_children() { return this.children; }

	@Override
	public int number_of_children() { return this.children.size(); }

	@Override
	public CirNode get_child(int k) throws IndexOutOfBoundsException { return this.children.get(k); }

	@Override
	public void add_child(CirNode child) throws IllegalArgumentException {
		if(child == null || child.get_parent() != null)
			throw new IllegalArgumentException("Invalid child: " + child);
		else {
			((CirNodeImpl) child).parent = this;
			((CirNodeImpl) child).child_index = this.children.size();
			this.children.add(child);
		}
	}

	@Override
	public String generate_code(boolean complete) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * @return clone an isolated node of this node
	 */
	protected abstract CirNode copy_self();
	
	@Override
	public CirNode clone() {
		CirNode parent = this.copy_self();
		for(CirNode child : this.children) {
			((CirNodeImpl) parent).add_child(child.clone());
		}
		return parent;
	}

	@Override
	public CirFunctionDefinition get_function_definition() {
		CirNode node = this;
		while(node != null) {
			if(node instanceof CirFunctionDefinition) {
				return (CirFunctionDefinition) node;
			}
			else {
				node = node.get_parent();
			}
		}
		return null;	// the node is an external unit
	}

}
