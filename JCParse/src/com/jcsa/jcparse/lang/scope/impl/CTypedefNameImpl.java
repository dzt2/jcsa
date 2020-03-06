package com.jcsa.jcparse.lang.scope.impl;

import com.jcsa.jcparse.lang.astree.base.AstIdentifier;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.scope.CScope;
import com.jcsa.jcparse.lang.scope.CTypedefName;

public class CTypedefNameImpl extends CTypeNameImpl implements CTypedefName {

	protected CTypedefNameImpl(CScope scope, AstIdentifier source) throws Exception {
		super(scope, source);
	}

	@Override
	public void set_type(CType type) {
		this.type = type;
	}

}
