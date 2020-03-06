package com.jcsa.jcparse.lang.scope.impl;

import com.jcsa.jcparse.lang.astree.base.AstIdentifier;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CUnionType;
import com.jcsa.jcparse.lang.scope.CScope;
import com.jcsa.jcparse.lang.scope.CUnionTypeName;

public class CUnionTypeNameImpl extends CTypeNameImpl implements CUnionTypeName {

	protected CUnionTypeNameImpl(CScope scope, AstIdentifier source) throws Exception {
		super(scope, source);
	}

	@Override
	public void set_type(CType type) {
		if (type != null && !(type instanceof CUnionType))
			throw new IllegalArgumentException("Invalid type: " + type);
		else
			this.type = type;
	}

}
