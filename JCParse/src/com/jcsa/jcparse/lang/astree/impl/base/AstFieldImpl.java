package com.jcsa.jcparse.lang.astree.impl.base;

import com.jcsa.jcparse.lang.astree.expr.othr.AstField;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.scope.CName;

public class AstFieldImpl extends AstFixedNode implements AstField {

	protected String field;

	public AstFieldImpl(String field) throws Exception {
		super(0);

		if (field == null || field.isEmpty())
			throw new IllegalArgumentException("Invalid field: null");
		else
			this.field = field;
	}

	@Override
	public String get_name() {
		return this.field;
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
