package com.jcsa.jcparse.lang.scope.impl;

import com.jcsa.jcparse.lang.astree.base.AstIdentifier;
import com.jcsa.jcparse.lang.ctype.CEnumType;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.scope.CEnumTypeName;
import com.jcsa.jcparse.lang.scope.CScope;

public class CEnumTypeNameImpl extends CTypeNameImpl implements CEnumTypeName {

	protected CEnumTypeNameImpl(CScope scope, AstIdentifier source) throws Exception {
		super(scope, source);
	}

	@Override
	public void set_type(CType type) {
		if (type != null && !(type instanceof CEnumType))
			throw new IllegalArgumentException("Invalid type: " + type);
		else
			this.type = type;
	}

}
