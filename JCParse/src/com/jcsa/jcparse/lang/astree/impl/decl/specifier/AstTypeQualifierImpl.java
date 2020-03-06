package com.jcsa.jcparse.lang.astree.impl.decl.specifier;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstTypeQualifier;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;

public class AstTypeQualifierImpl extends AstFixedNode implements AstTypeQualifier {

	public AstTypeQualifierImpl(AstKeyword keyword) throws Exception {
		super(1);

		if (keyword == null)
			throw new IllegalArgumentException("Invalid keyword: null");
		else {
			switch (keyword.get_keyword()) {
			case c89_const:
			case c89_volatile:
			case c99_restrict:
				this.set_child(0, keyword);
				break;
			default:
				throw new IllegalArgumentException("Invalid keyword: " + keyword);
			}
		}
	}

	@Override
	public AstKeyword get_keyword() {
		return (AstKeyword) children[0];
	}

}
