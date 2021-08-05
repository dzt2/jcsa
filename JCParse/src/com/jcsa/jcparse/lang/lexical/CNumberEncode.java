package com.jcsa.jcparse.lang.lexical;

/**
 * number of C can be encoded as <br>
 * 1. octal <br>
 * 2. decimal <br>
 * 3. hexical <br>
 *
 * @author yukimula
 *
 */
public enum CNumberEncode {
	/** 0, 05, 057 **/
	octal,
	/** 1, 5, 290 **/
	decimal,
	/** 0xAFFAC **/
	hexical,
}
