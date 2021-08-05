package com.jcsa.jcparse.lang.ptoken;

import com.jcsa.jcparse.lang.lexical.CPunctuator;

/**
 * token as punctuator at preprocessing
 *
 * @author yukimula
 *
 */
public interface PPunctuatorToken extends PToken {
	/**
	 * get punctuator of this token
	 *
	 * @return
	 */
	public CPunctuator get_punctuator();
}
