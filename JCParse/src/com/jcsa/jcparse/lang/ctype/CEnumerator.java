package com.jcsa.jcparse.lang.ctype;

/**
 * enumerator in enum type definition
 * 
 * @author yukimula
 */
public interface CEnumerator {
	/**
	 * get the type where the enumerator is defined
	 * 
	 * @return
	 */
	public CEnumType get_origin();

	/**
	 * get the name of this enumerator
	 * 
	 * @return
	 */
	public String get_literal();

	/**
	 * get the value of this enumerator
	 * 
	 * @return
	 */
	public int get_value();
}
