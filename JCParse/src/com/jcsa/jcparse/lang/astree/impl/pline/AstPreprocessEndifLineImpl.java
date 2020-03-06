package com.jcsa.jcparse.lang.astree.impl.pline;

import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.pline.AstDirective;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessEndifLine;
import com.jcsa.jcparse.lang.lexical.CDirective;

public class AstPreprocessEndifLineImpl extends AstFixedNode implements AstPreprocessEndifLine {

	public AstPreprocessEndifLineImpl(AstDirective endif) throws Exception {
		super(1);

		if (endif == null || endif.get_directive() != CDirective.cdir_endif)
			throw new IllegalArgumentException("Invalid #endif: null");
		else
			this.set_child(0, endif);
	}

	@Override
	public AstDirective get_directive() {
		return (AstDirective) children[0];
	}

}
