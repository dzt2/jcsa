package com.jcsa.jcparse.lang.ctype;

/**
 * field defined in struct | union body
 *
 * @author yukimula
 */
public interface CField {
	/** prefix for field of unknown name **/
	public static final String Unknown_Prefix = "#unknown_";

	/**
	 * get the type where the field is defined
	 *
	 * @return
	 */
	public CType get_origin();

	/**
	 * get the name of this field in body
	 *
	 * @return
	 */
	public String get_name();

	/**
	 * get the type of this field
	 *
	 * @return
	 */
	public CType get_type();

	/**
	 * get the number of bits required to be captured
	 *
	 * @return : -1 if no bit-size is specified by users
	 */
	public int get_bitsize();
}
