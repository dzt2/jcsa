package com.jcsa.jcparse.lang.ir.impl;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.CirTree;
import com.jcsa.jcparse.lang.ir.unit.CirLabel;

public class CirLabelImpl extends CirNodeImpl implements CirLabel {
	
	private int label;

	protected CirLabelImpl(CirTree tree) {
		super(tree);
		this.label = -1;
	}

	@Override
	public int get_label() { return this.label; }

	@Override
	public void set_label(int label) { this.label = label; }

	@Override
	protected CirNode copy_self() {
		CirLabelImpl label = new CirLabelImpl(this.get_tree());
		label.label = this.label;
		return label;
	}

}
