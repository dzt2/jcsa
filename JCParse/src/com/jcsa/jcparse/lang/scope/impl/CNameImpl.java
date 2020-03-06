package com.jcsa.jcparse.lang.scope.impl;

import com.jcsa.jcparse.lang.astree.base.AstIdentifier;
import com.jcsa.jcparse.lang.scope.CName;
import com.jcsa.jcparse.lang.scope.CScope;

public abstract class CNameImpl implements CName {

	protected CScope scope;
	protected AstIdentifier source;
	protected CNameLinkage linkage;

	protected CNameImpl(CScope scope, AstIdentifier source) throws Exception {
		if (scope == null)
			throw new IllegalArgumentException("Invalid scope: null");
		else if (source == null)
			throw new IllegalArgumentException("Invalid source: null");
		else {
			this.scope = scope;
			this.source = source;
			this.linkage = CNameLinkage.no_linkage;
		}
	}

	@Override
	public CScope get_scope() {
		return scope;
	}

	@Override
	public String get_name() {
		return source.get_name();
	}

	@Override
	public AstIdentifier get_source() {
		return source;
	}

	@Override
	public void set_source(AstIdentifier source) {
		this.source = source;
	}

	@Override
	public CNameLinkage get_linkage() {
		return linkage;
	}

	@Override
	public void set_linkage(CNameLinkage linkage) throws Exception {
		if (linkage == null)
			throw new IllegalArgumentException("Invalid linkage: null");
		else
			this.linkage = linkage;
	}

}
