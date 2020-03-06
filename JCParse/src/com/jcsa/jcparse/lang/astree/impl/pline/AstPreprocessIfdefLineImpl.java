package com.jcsa.jcparse.lang.astree.impl.pline;

import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.pline.AstDirective;
import com.jcsa.jcparse.lang.astree.pline.AstMacro;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessIfdefLine;
import com.jcsa.jcparse.lang.lexical.CDirective;

public class AstPreprocessIfdefLineImpl extends AstFixedNode implements AstPreprocessIfdefLine {

	public AstPreprocessIfdefLineImpl(AstDirective ifdef, AstMacro macro) throws Exception {
		super(2);

		if (ifdef == null || ifdef.get_directive() != CDirective.cdir_ifdef)
			throw new IllegalArgumentException("Invalid #ifdef: null");
		else {
			this.set_child(0, ifdef);
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
