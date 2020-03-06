package com.jcsa.jcparse.lang.astree.impl.pline;

import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.pline.AstDirective;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessElseLine;
import com.jcsa.jcparse.lang.lexical.CDirective;

public class AstPreprocessElseLineImpl extends AstFixedNode implements AstPreprocessElseLine {

	public AstPreprocessElseLineImpl(AstDirective _else) throws Exception {
		super(1);

		if (_else == null || _else.get_directive() != CDirective.cdir_else)
			throw new IllegalArgumentException("Invalid #else: null");
		else
			this.set_child(0, _else);
	}

	@Override
	public AstDirective get_directive() {
		return (AstDirective) children[0];
	}

}
