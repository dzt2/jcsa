package com.jcsa.jcparse.lang.ptoken;

/**
 * token as character constant at preprocessing: <br>
 * character |--> (L)? ' c-char-sequence ' <br>
 *
 * @author yukimula
 *
 */
public interface PCharacterToken extends PToken {
	/**
	 * get char sequence within '...'
	 *
	 * @return
	 */
	public String get_char_sequence();

	/**
	 * whether there is 'L' prefix
	 *
	 * @return
	 */
	public boolean is_widen();
}
