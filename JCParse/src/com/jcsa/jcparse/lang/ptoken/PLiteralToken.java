package com.jcsa.jcparse.lang.ptoken;

/**
 * token as string literal at preprocessing<br>
 * literal |--> (L)? " (c-char-sequence)* "
 *
 * @author yukimula
 *
 */
public interface PLiteralToken extends PToken {
	/**
	 * whether prefix is 'L';
	 *
	 * @return
	 */
	public boolean is_widen();

	/**
	 * get the literal between " and " (without translating)
	 *
	 * @return
	 */
	public String get_literal();
}
