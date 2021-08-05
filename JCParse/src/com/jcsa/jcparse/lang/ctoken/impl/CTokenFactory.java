package com.jcsa.jcparse.lang.ctoken.impl;

import com.jcsa.jcparse.lang.ctoken.CConstantToken;
import com.jcsa.jcparse.lang.ctoken.CDirectiveToken;
import com.jcsa.jcparse.lang.ctoken.CHeaderToken;
import com.jcsa.jcparse.lang.ctoken.CIdentifierToken;
import com.jcsa.jcparse.lang.ctoken.CKeywordToken;
import com.jcsa.jcparse.lang.ctoken.CLiteralToken;
import com.jcsa.jcparse.lang.ctoken.CNewlineToken;
import com.jcsa.jcparse.lang.ctoken.CPunctuatorToken;
import com.jcsa.jcparse.lang.lexical.CDirective;
import com.jcsa.jcparse.lang.lexical.CKeyword;
import com.jcsa.jcparse.lang.lexical.CPunctuator;

/**
 * to generate CToken
 *
 * @author yukimula
 */
public class CTokenFactory {
	/**
	 * constructor
	 */
	public CTokenFactory() {
	}

	public CIdentifierToken new_identifier(String name) {
		return new CIdentifierTokenImpl(name);
	}

	public CKeywordToken new_keyword(CKeyword keyword) {
		return new CKeywordTokenImpl(keyword);
	}

	public CDirectiveToken new_directive(CDirective dir) {
		return new CDirectiveTokenImpl(dir);
	}

	public CHeaderToken new_system_header(String path) {
		return new CHeaderTokenImpl(true, path);
	}

	public CHeaderToken new_user_header(String path) {
		return new CHeaderTokenImpl(false, path);
	}

	public CNewlineToken new_newline() {
		return new CNewlineTokenImpl();
	}

	public CConstantToken new_constant() {
		return new CConstantTokenImpl();
	}

	public CLiteralToken new_literal(String lit) {
		return new CLiteralTokenImpl(false, lit);
	}

	public CLiteralToken new_widen_literal(String lit) {
		return new CLiteralTokenImpl(true, lit);
	}

	public CPunctuatorToken new_punctuator(CPunctuator punc) {
		return new CPunctuatorTokenImpl(punc);
	}

}
