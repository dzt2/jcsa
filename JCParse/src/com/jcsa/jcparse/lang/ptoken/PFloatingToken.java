package com.jcsa.jcparse.lang.ptoken;

import com.jcsa.jcparse.lang.lexical.CNumberEncode;

/**
 * token for floating constant at preprocessing<br>
 * real_constant |--> floating suffix <br>
 * floating |--> (integer_part)? . fraction_part ((e|E) (+|-)? exponent_part)?
 * <br>
 * |--> integer_part . ((e|E) (+|-)? exponent_part)? <br>
 * suffix |--> f | F | l | L <br>
 * 
 * @author yukimula
 *
 */
public interface PFloatingToken extends PToken {
	/**
	 * get the number encode
	 * 
	 * @return
	 */
	public CNumberEncode get_encode();

	/**
	 * get the integer part literal
	 * 
	 * @return
	 */
	public String get_integer_part();

	/**
	 * get the fraction part literal
	 * 
	 * @return
	 */
	public String get_fraction_part();

	/**
	 * true if positive, false if negative
	 * 
	 * @return
	 */
	public boolean get_exponent_sign();

	/**
	 * get the exponent part literal
	 * 
	 * @return
	 */
	public String get_exponent_part();

	/**
	 * get the floating suffix: f | F | d | D | l | L
	 * 
	 * @return
	 */
	public char get_floating_suffix();
}
