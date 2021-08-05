package com.jcsa.jcparse.lang.ctoken;

/**
 * token to represent string literal in C program
 *
 * @author yukimula
 */
public interface CLiteralToken extends CToken {
	/**
	 * whether the string needs to be stored in widen format.
	 *
	 * @return
	 */
	public boolean is_widen();

	/**
	 * Get the literal that directly represent the bytes in executional
	 * environment. <br>
	 * This will translate the escape character to actual character encode.
	 *
	 * @return
	 */
	public String get_exec_literal();
}
