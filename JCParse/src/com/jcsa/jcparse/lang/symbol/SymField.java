package com.jcsa.jcparse.lang.symbol;

/**
 * 	Field	|--	{name}
 * @author yukimula
 *
 */
public interface SymField extends SymNode {
	
	/**
	 * get the field name used to interpret field-expression
	 * @return
	 */
	public String get_field_name();
	
}
