package com.jcsa.jcparse.lang.ctoken;

import com.jcsa.jcparse.lang.lexical.CDirective;

/**
 * token to represent directive in C program
 *
 * @author yukimula
 */
public interface CDirectiveToken extends CToken {
	/**
	 * get directive of this token
	 *
	 * @return
	 */
	public CDirective get_directive();
}
