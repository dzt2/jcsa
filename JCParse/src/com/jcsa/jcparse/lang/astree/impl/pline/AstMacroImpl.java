package com.jcsa.jcparse.lang.astree.impl.pline;

import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.pline.AstMacro;
import com.jcsa.jcparse.lang.scope.CName;

public class AstMacroImpl extends AstFixedNode implements AstMacro {

	private String name;

	public AstMacroImpl(String name) throws Exception {
		super(0);

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
