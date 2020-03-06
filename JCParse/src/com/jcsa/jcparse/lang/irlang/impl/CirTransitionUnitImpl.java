package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.unit.CirExternalUnit;
import com.jcsa.jcparse.lang.irlang.unit.CirTransitionUnit;

public class CirTransitionUnitImpl extends CirNodeImpl implements CirTransitionUnit {

	protected CirTransitionUnitImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, false);
	}

	@Override
	public int number_of_units() {
		return this.number_of_children();
	}
	@Override
	public CirExternalUnit get_unit(int k) throws IndexOutOfBoundsException {
		return (CirExternalUnit) this.get_child(k);
	}
	@Override
	public void add_unit(CirExternalUnit unit) throws IllegalArgumentException {
		this.add_child((CirNodeImpl) unit);
	}

}
