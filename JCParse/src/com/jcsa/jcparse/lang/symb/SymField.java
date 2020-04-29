package com.jcsa.jcparse.lang.symb;

/**
 * 	Field	|--	{identifier}
 *	@author yukimula
 *
 */
public class SymField extends SymNode {
	
	/** field name **/
	private String name;
	
	/**
	 * create a field for field-expression
	 * @param name
	 * @throws IllegalArgumentException
	 */
	protected SymField(String name) throws IllegalArgumentException {
		if(name == null || name.isBlank())
			throw new IllegalArgumentException("Invalid name: null");
		else this.name = name;
	}
	
	/**
	 * get the name of the field
	 * @return
	 */
	public String get_name() { return this.name; }
	
	@Override
	public String toString() { return this.name; }
	
}
