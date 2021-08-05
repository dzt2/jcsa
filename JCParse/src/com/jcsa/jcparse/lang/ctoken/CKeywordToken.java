package com.jcsa.jcparse.lang.ctoken;

import com.jcsa.jcparse.lang.lexical.CKeyword;

/**
 * @see CKeyword
 * @author yukimula
 *
 */
public interface CKeywordToken extends CToken {
	/**
	 * get the keyword hold by this token
	 *
	 * @return
	 */
	public CKeyword get_keyword();
}
