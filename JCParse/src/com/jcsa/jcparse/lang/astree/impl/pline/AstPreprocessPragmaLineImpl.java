package com.jcsa.jcparse.lang.astree.impl.pline;

import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.pline.AstDirective;
import com.jcsa.jcparse.lang.astree.pline.AstMacroBody;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessPragmaLine;
import com.jcsa.jcparse.lang.lexical.CDirective;

public class AstPreprocessPragmaLineImpl extends AstFixedNode implements AstPreprocessPragmaLine {

	public AstPreprocessPragmaLineImpl(AstDirective pragma, AstMacroBody body) throws Exception {
		super(2);

		if (pragma == null || pragma.get_directive() != CDirective.cdir_pragma)
			throw new IllegalArgumentException("Invalid #pragma: null");
		else {
			this.set_child(0, pragma);
			this.set_child(1, body);
		}
	}

	@Override
	public AstDirective get_directive() {
		return (AstDirective) children[0];
	}

	@Override
	public AstMacroBody get_body() {
		return (AstMacroBody) children[1];
	}

}
