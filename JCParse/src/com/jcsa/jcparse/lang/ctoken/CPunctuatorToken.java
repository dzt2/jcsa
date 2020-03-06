package com.jcsa.jcparse.lang.ctoken;

import com.jcsa.jcparse.lang.lexical.CPunctuator;

/**
 * token to represent punctuator in C program
 * 
 * @author yukimula
 */
public interface CPunctuatorToken extends CToken {
	/**
	 * get the punctuator of this token
	 * 
	 * @return
	 */
	public CPunctuator get_punctuator();
}
