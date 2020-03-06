package com.jcsa.jcparse.lang.ctoken.impl;

import com.jcsa.jcparse.lang.ctoken.CKeywordToken;
import com.jcsa.jcparse.lang.lexical.CKeyword;

public class CKeywordTokenImpl extends CTokenImpl implements CKeywordToken {

	/** keyword of this token **/
	private CKeyword keyword;

	protected CKeywordTokenImpl(CKeyword keyword) {
		super();
		if (keyword == null)
			throw new IllegalArgumentException("Invalid keyword: null");
		else
			this.keyword = keyword;
	}

	@Override
	public CKeyword get_keyword() {
		return keyword;
	}

	@Override
	public String toString() {
		return "<KW>::" + keyword.toString().substring(4);
	}
}
