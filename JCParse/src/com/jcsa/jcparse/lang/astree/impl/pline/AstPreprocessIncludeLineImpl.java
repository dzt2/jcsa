package com.jcsa.jcparse.lang.astree.impl.pline;

import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.astree.pline.AstDirective;
import com.jcsa.jcparse.lang.astree.pline.AstHeader;
import com.jcsa.jcparse.lang.astree.pline.AstPreprocessIncludeLine;
import com.jcsa.jcparse.lang.lexical.CDirective;

public class AstPreprocessIncludeLineImpl extends AstFixedNode implements AstPreprocessIncludeLine {

	public AstPreprocessIncludeLineImpl(AstDirective include, AstHeader header) throws Exception {
		super(2);

		if (include == null || include.get_directive() != CDirective.cdir_include)
			throw new IllegalArgumentException("Invalid #include: null");
		else {
			this.set_child(0, include);
			this.set_child(1, header);
		}
	}

	@Override
	public AstDirective get_directive() {
		return (AstDirective) children[0];
	}

	@Override
	public AstHeader get_header() {
		return (AstHeader) children[1];
	}

}
