package com.jcsa.jcparse.lang.lexical.keywords;

import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.CLangKeywordLib;
import com.jcsa.jcparse.lang.lexical.CLangKeywordLoader;

/**
 * load C99-specific keywords in the keyword lib
 * 
 * @author yukimula
 *
 */
public class C99KeywordLoader implements CLangKeywordLoader {

	protected C99KeywordLoader() {
	}

	@Override
	public void load(CLangKeywordLib lib) throws Exception {
		lib.insert_keyword("inline", CKeyword.c99_inline);
		lib.insert_keyword("restrict", CKeyword.c99_restrict);
		lib.insert_keyword("_Bool", CKeyword.c99_bool);
		lib.insert_keyword("_Complex", CKeyword.c99_complex);
		lib.insert_keyword("_Imaginary", CKeyword.c99_imaginary);
	}

}
