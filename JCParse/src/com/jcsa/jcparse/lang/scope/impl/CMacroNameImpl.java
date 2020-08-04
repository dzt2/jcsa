package com.jcsa.jcparse.lang.scope.impl;

import com.jcsa.jcparse.lang.astree.base.AstIdentifier;
import com.jcsa.jcparse.lang.scope.CMacro;
import com.jcsa.jcparse.lang.scope.CMacroName;
import com.jcsa.jcparse.lang.scope.CScope;

public class CMacroNameImpl extends CNameImpl implements CMacroName {

	protected CMacroNameImpl(CScope scope, AstIdentifier source) throws Exception {
		super(scope, source);
		macro = null;
	}

	protected CMacro macro;

	@Override
	public CMacro get_macro() {
		return macro;
	}

	@Override
	public void set_macro(CMacro macro) {
		this.macro = macro;
	}

}
