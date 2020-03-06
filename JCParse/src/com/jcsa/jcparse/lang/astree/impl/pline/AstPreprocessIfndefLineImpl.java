package com.jcsa.jcparse.lang.astree.impl.pline;

import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.pline.AstDirective;
import com.jcsa.jcparse.lang.astree.pline.AstMacro;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessIfndefLine;
import com.jcsa.jcparse.lang.lexical.CDirective;

public class AstPreprocessIfndefLineImpl extends AstFixedNode implements AstPreprocessIfndefLine {

	public AstPreprocessIfndefLineImpl(AstDirective ifndef, AstMacro macro) throws Exception {
		super(2);

		if (ifndef == null || ifndef.get_directive() != CDirective.cdir_ifndef)
			throw new IllegalArgumentException("Invalid #ifndef: null");
		else {
			this.set_child(0, ifndef);
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
