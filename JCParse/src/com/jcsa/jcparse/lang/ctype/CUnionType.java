package com.jcsa.jcparse.lang.ctype;

/**
 * <code>(struct (field)+)</code>
 *
 * @author yukimula
 *
 */
public interface CUnionType extends CType {
	public String get_name();
	/**
	 * get the field body
	 *
	 * @return
	 */
	public CFieldBody get_fields();

}
