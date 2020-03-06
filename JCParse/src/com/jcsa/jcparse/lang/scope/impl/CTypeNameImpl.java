package com.jcsa.jcparse.lang.scope.impl;

import com.jcsa.jcparse.lang.astree.base.AstIdentifier;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.scope.CScope;
import com.jcsa.jcparse.lang.scope.CTypeName;

public abstract class CTypeNameImpl extends CNameImpl implements CTypeName {

	protected CTypeNameImpl(CScope scope, AstIdentifier source) throws Exception {
		super(scope, source);
		type = null;
	}

	protected CType type;

	@Override
	public CType get_type() {
		return type;
	}

}
