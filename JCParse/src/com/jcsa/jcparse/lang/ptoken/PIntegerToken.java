package com.jcsa.jcparse.lang.ptoken;

import com.jcsa.jcparse.lang.lexical.CNumberEncode;

/**
 * token as integer constant at preprocessing: <br>
 * <br>
 * integer |--> 0 (0-7)* int_suffix <br>
 * |--> (1-9) (0-9)* int_suffix <br>
 * |--> 0 (x|X) (0-9|a-f|A-F)* int_suffix <br>
 * <br>
 * int_suffix |--> (u|U)? (l|ll|L|LL)? <br>
 * |--> (l|ll|L|LL)? (u|U)? <br>
 * 
 * @author yukimula
 *
 */
public interface PIntegerToken extends PToken {
	/**
	 * get the encode of this integer
	 * 
	 * @return
	 */
	public CNumberEncode get_encode();

	/**
	 * get the number literal of this integer
	 * 
	 * @return
	 */
	public String get_int_literal();

	/**
	 * get the suffix of integer
	 * 
	 * @return
	 */
	public String get_int_suffix();
}
