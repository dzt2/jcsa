package com.jcsa.jcparse.lang.ptoken;

import com.jcsa.jcparse.lang.lexical.CDirective;

/**
 * token as directive in preprocessing
 *
 * @author yukimula
 *
 */
public interface PDirectiveToken extends PToken {
	/**
	 * get the directive of this token
	 *
	 * @return
	 */
	public CDirective get_directive();
}
