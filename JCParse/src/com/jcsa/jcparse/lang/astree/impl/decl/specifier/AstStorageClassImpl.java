package com.jcsa.jcparse.lang.astree.impl.decl.specifier;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.decl.specifier.AstStorageClass;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;

public class AstStorageClassImpl extends AstFixedNode implements AstStorageClass {

	public AstStorageClassImpl(AstKeyword keyword) throws Exception {
		super(1);

		switch (keyword.get_keyword()) {
		case c89_typedef:
		case c89_auto:
		case c89_register:
		case c89_static:
		case c89_extern:
			this.set_child(0, keyword);
			break;
		default:
			throw new IllegalArgumentException("Invalid keyword: " + keyword);
		}
	}

	@Override
	public AstKeyword get_keyword() {
		return (AstKeyword) children[0];
	}

}
