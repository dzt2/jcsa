package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * address is symbol
 * @author yukimula
 *
 */
public class SymAddress extends SymBasicExpression {

	protected SymAddress(CType data_type, String identifier) throws IllegalArgumentException {
		super(data_type, identifier);
		if(identifier == null || identifier.isBlank())
			throw new IllegalArgumentException("Invalid identifier");
	}
	
	/**
	 * get the symbolic address
	 * @return
	 */
	public String get_address() { return (String) this.value; }
	
	@Override
	public String toString() { return this.get_address(); }
	
}
