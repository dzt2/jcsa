package com.jcsa.jcparse.lang.ptoken;

/**
 * token as comment in preprocessing
 *
 * @author yukimula
 *
 */
public interface PCommentToken extends PToken {
	/**
	 * is this a block comment
	 *
	 * @return
	 */
	public boolean is_block_comment();

	/**
	 * is this a line comment
	 *
	 * @return
	 */
	public boolean is_line_comment();

	/**
	 * get the content in comment
	 *
	 * @return
	 */
	public String get_comment();
}
