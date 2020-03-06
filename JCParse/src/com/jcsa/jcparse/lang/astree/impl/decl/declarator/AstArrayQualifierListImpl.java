package com.jcsa.jcparse.lang.astree.impl.decl.declarator;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.decl.declarator.AstArrayQualifierList;
import com.jcsa.jcparse.lang.astree.impl.AstVariableNode;

public class AstArrayQualifierListImpl extends AstVariableNode implements AstArrayQualifierList {

	public AstArrayQualifierListImpl(AstKeyword keyword) throws Exception {
		super();
		this.append_keyword(keyword);
	}

	@Override
	public int number_of_keywords() {
		return children.size();
	}

	@Override
	public AstKeyword get_keyword(int k) {
		if (k < 0 || k >= children.size())
			throw new IllegalArgumentException("Invalid index: k = " + k);
		else
			return (AstKeyword) children.get(k);
	}

	@Override
	public void append_keyword(AstKeyword keyword) throws Exception {
		if (keyword == null)
			throw new IllegalArgumentException("Invalid keyword: null");
		else {
			switch (keyword.get_keyword()) {
			case c89_static:
			case c89_const:
			case c89_volatile:
			case c99_restrict:
				this.append_child(keyword);
				break;
			default:
				throw new IllegalArgumentException("Invalid keyword: " + keyword.get_keyword());
			}
		}
	}

}
