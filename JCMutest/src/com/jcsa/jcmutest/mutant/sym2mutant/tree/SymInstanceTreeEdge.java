package com.jcsa.jcmutest.mutant.sym2mutant.tree;

import com.jcsa.jcmutest.mutant.sym2mutant.base.SymInstance;

/**
 * The edge in the symbolic instance tree w.r.t. a constraint
 * 
 * @author yukimula
 *
 */
public class SymInstanceTreeEdge extends SymInstanceNode {
	
	/* definitions */
	private SymInstanceTreeNode parent;
	private SymInstanceTreeNode child;
	private int index;
	protected SymInstanceTreeEdge(SymInstanceTreeNode parent, SymInstanceTreeNode child, 
			int index, SymInstance instance) throws Exception {
		super(instance);
		if(parent == null)
			throw new IllegalArgumentException("Invalid parent: null");
		else if(child == null)
			throw new IllegalArgumentException("Invalid child as null");
		else if(index < 0)
			throw new IllegalArgumentException("Invalid index: " + index);
		else {
			this.parent = parent;
			this.child = child;
			this.index = index;
		}
	}
	
	/* getters */
	/**
	 * @return the parent node of the edge
	 */
	public SymInstanceTreeNode get_parent() { return this.parent; }
	/**
	 * @return the direct child of the edge
	 */
	public SymInstanceTreeNode get_child() { return this.child; }
	/**
	 * @return the index of the child node under the parent
	 */
	public int get_index() { return this.index; }
	
}
