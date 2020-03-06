package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.stmt.CirLabel;

public class CirLabelImpl extends CirNodeImpl implements CirLabel {

	protected CirLabelImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
		this.target_node_id = -1;
	}

	private int target_node_id;
	@Override
	public int get_target_node_id() { return this.target_node_id; }
	@Override
	public void set_target_node_id(int id) {
		if(id >= 0) {
			this.target_node_id = id;
		}
		else {
			throw new IllegalArgumentException("invalid: " + id);
		}
	}

}
