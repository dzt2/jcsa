package com.jcsa.jcparse.lang.astree.impl;

import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.text.CLocation;

/**
 * Node of which children size can be pre-determined
 *
 * @author yukimula
 *
 */
public abstract class AstFixedNode extends AstNodeImpl {

	protected AstNode[] children;

	protected AstFixedNode(int size) throws Exception {
		super();

		this.children = null;
		if (size > 0) {
			this.children = new AstNode[size];
			for (int i = 0; i < size; i++)
				this.children[i] = null;
		}
	}

	@Override
	public int number_of_children() {
		if (children == null)
			return 0;
		else
			return children.length;
	}

	@Override
	public AstNode get_child(int k) {
		if (children == null)
			return null;
		else if (k < 0 || k >= children.length)
			return null;
		else
			return this.children[k];
	}

	/**
	 * set the kth child in fixed children list, and fails in one of following
	 * cases:<br>
	 * 1. <i>k</i> is out of index; <br>
	 * 2. <i>child</i> is null; <br>
	 * 3. kth child was established; <br>
	 * 4. child.parent has been set. <br>
	 * this method will update the location when head and tail are
	 * available.<br>
	 *
	 * @param k
	 * @param child
	 */
	protected void set_child(int k, AstNode child) throws Exception {
		if (children == null)
			throw new IllegalArgumentException("Invalid access: no children");
		else if (k < 0 || k >= children.length)
			throw new IllegalArgumentException("Index out of bound: " + k);
		else if (child == null)
			throw new IllegalArgumentException("Invalid child: null");
		else if (children[k] != null)
			throw new IllegalArgumentException("Duplicated child");
		else {
			if (child instanceof AstNodeImpl)
				((AstNodeImpl) child).set_parent(this);
			this.children[k] = child;
			this.update_location(); /* automatically update location */
		}
	}

	/**
	 * Update the location when children[0] and children[n - 1] are not null.
	 */
	private void update_location() {
		if (children == null)
			return;
		else if (children[0] == null)
			return;
		else if (children[children.length - 1] == null)
			return;
		else if (location != null)
			return;
		else {
			int beg = children[0].get_location().get_bias();
			CLocation end_loc = children[children.length - 1].get_location();
			int end = end_loc.get_bias() + end_loc.get_length();
			this.location = end_loc.get_source().get_location(beg, end - beg);
		}
	}
}
