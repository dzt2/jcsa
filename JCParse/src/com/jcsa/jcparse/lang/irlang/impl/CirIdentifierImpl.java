package com.jcsa.jcparse.lang.irlang.impl;

import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirIdentifier;
import com.jcsa.jcparse.lang.scope.CName;

public class CirIdentifierImpl extends CirExpressionImpl implements CirIdentifier {

	protected CirIdentifierImpl(CirTree tree, int node_id) throws IllegalArgumentException {
		super(tree, node_id, true);
	}

	private CName name;
	@Override
	public String get_name() {
		if(name == null) {
			return null;
		}
		else {
			return name.get_name();
		}
	}
	@Override
	public CName get_cname() {
		return this.name;
	}
	@Override
	public void set_cname(CName cname) throws IllegalArgumentException {
		if(cname == null) {
			throw new IllegalArgumentException("invalid cname: null");
		}
		else {
			this.name = cname;
		}
	}
	@Override
	public String get_unique_name() {
		return name.get_name() + "#" + name.get_scope().hashCode();
	}

}
