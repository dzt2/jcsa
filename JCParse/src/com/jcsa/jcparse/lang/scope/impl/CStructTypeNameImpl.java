package com.jcsa.jcparse.lang.scope.impl;

import com.jcsa.jcparse.lang.astree.base.AstIdentifier;
import com.jcsa.jcparse.lang.ctype.CStructType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.scope.CScope;
import com.jcsa.jcparse.lang.scope.CStructTypeName;

public class CStructTypeNameImpl extends CTypeNameImpl implements CStructTypeName {

	protected CStructTypeNameImpl(CScope scope, AstIdentifier source) throws Exception {
		super(scope, source);
	}

	@Override
	public void set_type(CType type) {
		if (type != null && !(type instanceof CStructType))
			throw new IllegalArgumentException("Invalid type: " + type);
		else
			this.type = type;
	}

}
