package com.jcsa.jcparse.lang.ctype;

/**
 * <code>(ptr type)</code>
 * 
 * @author yukimula
 *
 */
public interface CPointerType extends CType {
	/**
	 * get the type of instance pointed by the instance of this type
	 * 
	 * @return
	 */
	public CType get_pointed_type();
}
