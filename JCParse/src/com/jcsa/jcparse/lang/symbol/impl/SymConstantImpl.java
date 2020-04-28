package com.jcsa.jcparse.lang.symbol.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.CConstant;
import com.jcsa.jcparse.lang.symbol.SymConstant;

public class SymConstantImpl extends SymExpressionImpl implements SymConstant {
	
	private CConstant constant;
	protected SymConstantImpl(CType data_type, CConstant constant) throws IllegalArgumentException {
		super(data_type);
		if(constant == null)
			throw new IllegalArgumentException("Invalid constant: null");
		else { this.constant = constant; }
	}

	@Override
	public CConstant get_constant() { return this.constant; }
	
	@Override
	public String toString() { return this.constant.toString(); }
	
}
