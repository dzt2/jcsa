package com.jcsa.jcparse.lang.scope;

import com.jcsa.jcparse.lang.ctoken.CToken;

/**
 * macro is a name representing a list of tokens in preprocessing phasis
 *
 * @author yukimula
 */
public interface CMacro {
	/**
	 * get the name of macro
	 *
	 * @return
	 */
	public String get_macro_name();

	/**
	 * get the list of tokens it defines
	 *
	 * @return
	 */
	public CToken[] get_token_list();
}
