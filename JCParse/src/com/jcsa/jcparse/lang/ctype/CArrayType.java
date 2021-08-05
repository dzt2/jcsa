package com.jcsa.jcparse.lang.ctype;

/**
 * <code>(array, n, type)</code>
 *
 * @author yukimula
 *
 */
public interface CArrayType extends CType {
	/**
	 * <code>get the number of elements in arrary of this type</code>
	 *
	 * @return
	 */
	public int length();

	/**
	 * get the type of element in array of this type
	 *
	 * @return
	 */
	public CType get_element_type();
}
