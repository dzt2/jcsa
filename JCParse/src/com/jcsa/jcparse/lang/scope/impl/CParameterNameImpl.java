package com.jcsa.jcparse.lang.scope.impl;

import com.jcsa.jcparse.lang.astree.base.AstIdentifier;
import com.jcsa.jcparse.lang.centity.CInstance;
import com.jcsa.jcparse.lang.scope.CParameterName;
import com.jcsa.jcparse.lang.scope.CScope;

public class CParameterNameImpl extends CNameImpl implements CParameterName {

	protected CParameterNameImpl(CScope scope, AstIdentifier source) throws Exception {
		super(scope, source);
		parameter = null;
	}

	protected CInstance parameter;

	@Override
	public CInstance get_parameter() {
		return parameter;
	}

	@Override
	public void set_parameter(CInstance param) {
		this.parameter = param;
	}

}
