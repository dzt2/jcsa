package com.jcsa.jcparse.lang.symbol.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.symbol.SymAddress;

public class SymAddressImpl extends SymExpressionImpl implements SymAddress {
	
	private String identifier;
	protected SymAddressImpl(CType data_type, String identifier) throws IllegalArgumentException {
		super(data_type);
		if(identifier == null || identifier.isBlank())
			throw new IllegalArgumentException("Invalid identifier");
		else { this.identifier = identifier; }
	}

	@Override
	public String get_address_value() { return this.identifier; }
	
	@Override
	public String toString() {
		return this.identifier;
	}
	
}
