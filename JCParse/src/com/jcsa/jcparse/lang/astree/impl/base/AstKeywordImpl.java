package com.jcsa.jcparse.lang.astree.impl.base;

import com.jcsa.jcparse.lang.astree.base.AstKeyword;
import com.jcsa.jcparse.lang.astree.impl.AstFixedNode;
import com.jcsa.jcparse.lang.lexical.CKeyword;

public class AstKeywordImpl extends AstFixedNode implements AstKeyword {

	private CKeyword keyword;

	public AstKeywordImpl(CKeyword keyword) throws Exception {
		super(0);
		if (keyword == null)
			throw new IllegalArgumentException("keyword: null");
		else
			this.keyword = keyword;
	}

	@Override
	public CKeyword get_keyword() {
		return keyword;
	}

	@Override
	public String toString() {
		return "<keyword>::" + keyword.toString().substring(4);
	}
}
