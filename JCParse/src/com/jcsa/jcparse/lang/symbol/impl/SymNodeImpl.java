package com.jcsa.jcparse.lang.symbol.impl;

import java.util.LinkedList;
import java.util.List;

import com.jcsa.jcparse.lang.symbol.SymNode;

/**
 * symbolic node abstract implementation
 * @author yukimula
 *
 */
public abstract class SymNodeImpl implements SymNode {
	
	private SymNodeImpl parent;
	private List<SymNode> children;
	protected SymNodeImpl() {
		this.parent = null;
		this.children = new LinkedList<SymNode>();
	}

	@Override
	public SymNode get_parent() { return this.parent; }
	@Override
	public int number_of_children() { return this.children.size(); }
	@Override
	public Iterable<SymNode> get_children() { return children; }
	@Override
	public SymNode get_child(int k) throws IndexOutOfBoundsException {
		return this.children.get(k);
	}
	
	/**
	 * add a child in the node
	 * @param child
	 * @throws IllegalArgumentException
	 */
	protected void add_child(SymNodeImpl child) throws IllegalArgumentException {
		if(child == null || child.parent != null)
			throw new IllegalArgumentException("Invalid child: null");
		else {
			child.parent = this; this.children.add(child);
		}
	}
	
}
