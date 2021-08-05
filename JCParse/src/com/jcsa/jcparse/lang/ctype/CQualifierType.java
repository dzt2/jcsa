package com.jcsa.jcparse.lang.ctype;

import com.jcsa.jcparse.lang.lexical.CTypeQualifier;

/**
 * <code>const | volatile | restrict | inline (type)</code>
 *
 * @author yukimula
 *
 */
public interface CQualifierType extends CType {
	/**
	 * get the qualifier of this type
	 *
	 * @return
	 */
	public CTypeQualifier get_qualifier();

	/**
	 * get the type under described
	 *
	 * @return
	 */
	public CType get_reference();
}
