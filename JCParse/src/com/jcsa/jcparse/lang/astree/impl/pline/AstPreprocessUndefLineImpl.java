package com.jcsa.jcparse.lang.astree.impl.pline;

import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.pline.AstDirective;
import com.jcsa.jcparse.lang.astree.pline.AstMacro;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessUndefLine;
import com.jcsa.jcparse.lang.lexical.CDirective;

public class AstPreprocessUndefLineImpl extends AstFixedNode implements AstPreprocessUndefLine {

	public AstPreprocessUndefLineImpl(AstDirective undef, AstMacro macro) throws Exception {
		super(2);

		if (undef == null || undef.get_directive() != CDirective.cdir_undef)
			throw new IllegalArgumentException("Invalid #undef: null");
		else {
			this.set_child(0, undef);
			this.set_child(1, macro);
		}
	}

	@Override
	public AstDirective get_directive() {
		return (AstDirective) children[0];
	}

	@Override
	public AstMacro get_macro() {
		return (AstMacro) children[1];
	}

}
