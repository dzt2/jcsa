package com.jcsa.jcparse.lang.astree.impl.pline;

import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.pline.AstDirective;
import com.jcsa.jcparse.lang.astree.pline.AstMacroBody;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessErrorLine;
import com.jcsa.jcparse.lang.lexical.CDirective;

public class AstPreprocessErrorLineImpl extends AstFixedNode implements AstPreprocessErrorLine {

	public AstPreprocessErrorLineImpl(AstDirective error, AstMacroBody body) throws Exception {
		super(2);

		if (error == null || error.get_directive() != CDirective.cdir_error)
			throw new IllegalArgumentException("Invalid #error: null");
		else {
			this.set_child(0, error);
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
