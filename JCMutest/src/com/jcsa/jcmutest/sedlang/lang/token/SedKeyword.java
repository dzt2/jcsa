package com.jcsa.jcmutest.sedlang.lang.token;

import com.jcsa.jcmutest.sedlang.SedKeywords;
import com.jcsa.jcmutest.sedlang.lang.SedNode;

/**
 * keyword ==> {keyword: SedKeywords}
 * @author yukimula
 *
 */
public class SedKeyword extends SedToken {
	
	private SedKeywords keyword;
	public SedKeyword(SedKeywords keyword) throws IllegalArgumentException {
		if(keyword == null)
			throw new IllegalArgumentException("Invalid keyword: null");
		else
			this.keyword = keyword;
	}
	
	/**
	 * @return the keyword tag of the node
	 */
	public SedKeywords get_keyword() { return this.keyword; }
	
	@Override
	public String generate_code() throws Exception {
		return this.keyword.toString();
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedKeyword(this.keyword);
	}

}
