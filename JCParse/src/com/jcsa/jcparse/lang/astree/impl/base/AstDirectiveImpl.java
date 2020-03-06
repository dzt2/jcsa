package com.jcsa.jcparse.lang.astree.impl.base;

import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.pline.AstDirective;
import com.jcsa.jcparse.lang.lexical.CDirective;

public class AstDirectiveImpl extends AstFixedNode implements AstDirective {

	/** directive **/
	private CDirective directive;

	public AstDirectiveImpl(CDirective dir) throws Exception {
		super(0);
		if (dir == null)
			throw new IllegalArgumentException("Invalid directive: null");
		else
			this.directive = dir;

	}

	@Override
	public CDirective get_directive() {
		return directive;
	}

	@Override
	public String toString() {
		return "<directive>::#" + directive.toString().substring(5);
	}
}
