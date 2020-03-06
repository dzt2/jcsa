package com.jcsa.jcparse.lang.astree.impl.decl.specifier;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstFunctionQualifier;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.lexical.CKeyword;

public class AstFunctionQualifierImpl extends AstFixedNode implements AstFunctionQualifier {

	public AstFunctionQualifierImpl(AstKeyword keyword) throws Exception {
		super(1);

		if (keyword.get_keyword() != CKeyword.c99_inline)
			throw new IllegalArgumentException("Invalid keyword: inline expected");
		else
			this.set_child(0, keyword);
	}

	@Override
	public AstKeyword get_keyword() {
		return (AstKeyword) children[0];
	}

}
