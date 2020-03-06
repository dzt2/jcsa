package com.jcsa.jcparse.lang.ctoken;

/**
 * identifier |--> (_, a-z, A-Z) (_, a-z, A-Z, 0-9)*
 * 
 * @author yukimula
 */
public interface CIdentifierToken extends CToken {
	/**
	 * get the name of identifier token
	 * 
	 * @return
	 */
	public String get_name();
}
