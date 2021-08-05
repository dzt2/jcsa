package com.jcsa.jcparse.parse.tokenizer;

import com.jcsa.jcparse.lang.ctoken.CToken;

/**
 * From <code>CTokenStream</code>, one can access token in a stream way.
 *
 * @author yukimula
 *
 */
public interface CTokenStream {
	/**
	 * whether the current cursor refers to available token
	 *
	 * @return
	 */
	public boolean has_token();

	/**
	 * get the current cursor
	 *
	 * @return
	 */
	public int get_cursor();

	/**
	 * get the token referred by current cursor.
	 *
	 * @return : null when out of index
	 * @throws Exception
	 */
	public CToken get_token();

	/**
	 * move the cursor forward to the next token
	 *
	 * @return : false if no more token
	 * @throws Exception
	 *             : lexical errors
	 */
	public boolean consume() throws Exception;

	/**
	 * set the cursor to original location
	 *
	 * @param cursor
	 * @throws Exception
	 *             : cursor is out of index
	 */
	public void recover(int cursor) throws Exception;
}
