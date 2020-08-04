package com.jcsa.jcparse.lang.scope.impl;

import com.jcsa.jcparse.lang.astree.base.AstIdentifier;
import com.jcsa.jcparse.lang.scope.CLabel;
import com.jcsa.jcparse.lang.scope.CLabelName;
import com.jcsa.jcparse.lang.scope.CScope;

public class CLabelNameImpl extends CNameImpl implements CLabelName {

	protected CLabelNameImpl(CScope scope, AstIdentifier source) throws Exception {
		super(scope, source);
		label = null;
	}

	protected CLabel label;

	@Override
	public CLabel get_label() {
		return label;
	}

	@Override
	public void set_label(CLabel label) {
		this.label = label;
	}

}
