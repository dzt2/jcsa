package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * default_value ==> [null]
 * @author yukimula
 *
 */
public class SymDefaultValue extends SymBasicExpression {

	protected SymDefaultValue(CType data_type) throws IllegalArgumentException {
		super(data_type, null);
	}
	
	@Override
	public String toString() { return "[?]"; }
	
}
