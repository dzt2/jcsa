package com.jcsa.jcparse.lang.symbol.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.symbol.SymArgumentList;
import com.jcsa.jcparse.lang.symbol.SymCallExpression;
import com.jcsa.jcparse.lang.symbol.SymExpression;

public class SymCallExpressionImpl extends SymExpressionImpl implements SymCallExpression {

	protected SymCallExpressionImpl(CType data_type, SymExpression function) throws IllegalArgumentException {
		super(data_type);
		this.add_child((SymNodeImpl) function);
		this.add_child(new SymArgumentListImpl());
	}

	@Override
	public SymExpression get_function() { return (SymExpression) this.get_child(0); }
	@Override
	public SymArgumentList get_arguments() { return (SymArgumentList) this.get_child(1); }
	
	@Override
	public String toString() {
		return this.get_function().toString() + " " + this.get_arguments().toString();
	}
	
}
