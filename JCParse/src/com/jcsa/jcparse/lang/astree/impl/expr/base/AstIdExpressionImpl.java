package com.jcsa.jcparse.lang.astree.impl.expr.base;

import com.jcsa.jcparse.lang.astree.expr.base.AstIdExpression;
import com.jcsa.jcparse.lang.scope.CName;

public class AstIdExpressionImpl extends AstBasicExpressionImpl implements AstIdExpression {

	protected String name;

	public AstIdExpressionImpl(String name) throws Exception {
		super();
		if (name == null || name.isEmpty())
			throw new IllegalArgumentException("Invalid name: null");
		else
			this.name = name;
	}

	@Override
	public String get_name() {
		return name;
	}

	protected CName cname;

	@Override
	public CName get_cname() {
		return cname;
	}

	@Override
	public void set_cname(CName name) {
		this.cname = name;
	}
}
