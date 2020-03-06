package com.jcsa.jcparse.lang.scope.impl;

import com.jcsa.jcparse.lang.astree.base.AstIdentifier;
import com.jcsa.jcparse.lang.ctype.CEnumerator;
import com.jcsa.jcparse.lang.scope.CEnumeratorName;
import com.jcsa.jcparse.lang.scope.CScope;

public class CEnumeratorNameImpl extends CNameImpl implements CEnumeratorName {

	protected CEnumeratorNameImpl(CScope scope, AstIdentifier source) throws Exception {
		super(scope, source);
		enumerator = null;
	}

	protected CEnumerator enumerator;

	@Override
	public CEnumerator get_enumerator() {
		return enumerator;
	}

	@Override
	public void set_enumerator(CEnumerator e) {
		enumerator = e;
	}

}
