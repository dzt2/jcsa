package com.jcsa.jcparse.lang.lexical.keywords;

import com.jcsa.jcparse.lang.lexical.CLangKeywordLoader;

/**
 * get keyword-loader by this factory
 *
 * @author yukimula
 *
 */
public class CLangKeywordLoaderFactory {
	protected static C89KeywordLoader c89_loader;
	protected static C99KeywordLoader c99_loader;
	protected static GNUKeywordLoader gnu_loader;

	static {
		c89_loader = new C89KeywordLoader();
		c99_loader = new C99KeywordLoader();
		gnu_loader = new GNUKeywordLoader();
	}

	public static CLangKeywordLoader get_c89_keyword_loader() {
		return c89_loader;
	}

	public static CLangKeywordLoader get_c99_keyword_loader() {
		return c99_loader;
	}

	public static CLangKeywordLoader get_gnu_keyword_loader() {
		return gnu_loader;
	}
}
