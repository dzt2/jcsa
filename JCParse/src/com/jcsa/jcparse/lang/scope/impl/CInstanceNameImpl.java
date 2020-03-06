package com.jcsa.jcparse.lang.scope.impl;

import com.jcsa.jcparse.lang.astree.base.AstIdentifier;
import com.jcsa.jcparse.lang.centity.CInstance;
import com.jcsa.jcparse.lang.scope.CInstanceName;
import com.jcsa.jcparse.lang.scope.CScope;

public class CInstanceNameImpl extends CNameImpl implements CInstanceName {

	protected CInstance instance;
	protected CInstanceName next;

	protected CInstanceNameImpl(CScope scope, AstIdentifier source) throws Exception {
		super(scope, source);
		instance = null;
		next = null;
	}

	@Override
	public CInstance get_instance() {
		return instance;
	}

	@Override
	public void set_instance(CInstance instance) {
		this.instance = instance;
	}

	@Override
	public CInstanceName get_next_name() {
		return this.next;
	}

	@Override
	public void set_next_name(CInstanceName name) {
		this.next = name;
	}

}
