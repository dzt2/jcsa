package com.jcsa.jcparse.lang.ctoken;

import com.jcsa.jcparse.lang.lexical.CConstant;

/**
 * token to represent integer | character | floating constant
 *
 * @author yukimula
 */
public interface CConstantToken extends CToken {
	/**
	 * get the constant this token represents
	 *
	 * @return
	 */
	public CConstant get_constant();
}
