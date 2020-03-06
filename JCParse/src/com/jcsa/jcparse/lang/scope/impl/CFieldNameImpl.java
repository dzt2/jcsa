package com.jcsa.jcparse.lang.scope.impl;

import com.jcsa.jcparse.lang.astree.base.AstIdentifier;
import com.jcsa.jcparse.lang.ctype.CField;
import com.jcsa.jcparse.lang.scope.CFieldName;
import com.jcsa.jcparse.lang.scope.CScope;

public class CFieldNameImpl extends CNameImpl implements CFieldName {

	protected CFieldNameImpl(CScope scope, AstIdentifier source) throws Exception {
		super(scope, source);
		field = null;
	}

	protected CField field;

	@Override
	public CField get_field() {
		return field;
	}

	@Override
	public void set_field(CField field) {
		this.field = field;
	}

}
