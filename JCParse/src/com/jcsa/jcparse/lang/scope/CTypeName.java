package com.jcsa.jcparse.lang.scope;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * name to represent type, could be: struct, union, enum, typedef
 * 
 * @author yukimula
 */
public interface CTypeName extends CName {
	/**
	 * get the type of this name
	 * 
	 * @return
	 */
	public CType get_type();

	/**
	 * set the type for this name
	 * 
	 * @param type
	 */
	public void set_type(CType type);
}
