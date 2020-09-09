package com.jcsa.jcmutest.mutant.sec2mutant.lang.token;

import com.jcsa.jcmutest.mutant.sec2mutant.SecKeywords;

public class SecKeyword extends SecToken {
	
	private SecKeywords keyword;
	public SecKeyword(SecKeywords keyword) throws Exception {
		if(keyword == null)
			throw new IllegalArgumentException("Invalid keyword: null");
		else
			this.keyword = keyword;
	}
	
	public SecKeywords get_keyword() { return this.keyword; }

	@Override
	public String generate_code() throws Exception {
		return this.keyword.toString();
	}

}
