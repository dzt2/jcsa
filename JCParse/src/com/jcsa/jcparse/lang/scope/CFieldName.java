package com.jcsa.jcparse.lang.scope;

import com.jcsa.jcparse.lang.ctype.CField;

/**
 * name to represent field in struct | union body
 *
 * @author yukimula
 */
public interface CFieldName extends CName {
	/**
	 * get the name of this field
	 *
	 * @return
	 */
	public CField get_field();

	/**
	 * set the name for this field
	 *
	 * @param field
	 */
	public void set_field(CField field);
}
