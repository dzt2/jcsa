package com.jcsa.jcparse.lang.ptoken;

/**
 * token as identifier at preprocessing phasis: <br>
 * identifier |--> {_, a-z, A-Z} {_, a-z, A-Z, 0-9}* <br>
 *
 * @author yukimula
 *
 */
public interface PIdentifierToken extends PToken {
	/**
	 * get the name of identifier
	 *
	 * @return
	 */
	public String get_name();
}
