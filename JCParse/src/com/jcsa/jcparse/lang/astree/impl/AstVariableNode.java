package com.jcsa.jcparse.lang.astree.impl;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.text.CLocation;

/**
 * Node of which children size is undecidable
 * 
 * @author yukimula
 *
 */
public abstract class AstVariableNode extends AstNodeImpl {

	protected List<AstNode> children;

	protected AstVariableNode() throws Exception {
		super();
		this.children = new ArrayList<AstNode>();
	}

	@Override
	public int number_of_children() {
		return children.size();
	}

	@Override
	public AstNode get_child(int k) {
		if (k < 0 || k >= children.size())
			return null;
		else
			return children.get(k);
	}

	/**
	 * append the child at the tail of this node's children
	 * 
	 * @param child
	 * @throws Exception
	 *             : child is null, or child.parent is established
	 */
	protected void append_child(AstNode child) throws Exception {
		if (child == null)
			throw new IllegalArgumentException("Invalid child: null");
		else {
			if (child instanceof AstNodeImpl)
				((AstNodeImpl) child).set_parent(this);
			this.children.add(child);
			this.update_location(); /* automatically update */
		}
	}

	/**
	 * Update the location at the following cases:<br>
	 * 1. When first child is added, construct new location;<br>
	 * 2. When new child is appended, update location. <br>
	 * 
	 * @throws Exception
	 */
	private void update_location() throws Exception {
		if (children.size() == 1) {
			CLocation bloc = children.get(0).get_location();
			this.location = bloc.get_source().get_location(bloc.get_bias(), bloc.get_length());
		} else if (children.size() > 1) {
			CLocation eloc = children.get(children.size() - 1).get_location();
			int beg = this.location.get_bias(), end = eloc.get_bias() + eloc.get_length();
			this.location.set_location(beg, end - beg);
		}
	}

}
