package com.jcsa.jcmutest.mutant.sel2mutant.lang.token;

import com.jcsa.jcmutest.mutant.sel2mutant.SelKeywords;

public class SelKeyword extends SelToken {
	
	private SelKeywords keyword;
	public SelKeyword(SelKeywords keyword) throws Exception {
		if(keyword == null)
			throw new IllegalArgumentException("Invalid keyword");
		else 
			this.keyword = keyword;
	}
	
	/**
	 * @return the keyword that the node describes
	 */
	public SelKeywords get_keyword() { return this.keyword; }

	@Override
	public String generate_code() throws Exception {
		return this.keyword.toString();
	}

}
