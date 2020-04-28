package com.jcsa.jcparse.lang.symbol.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.symbol.SymCastExpression;
import com.jcsa.jcparse.lang.symbol.SymExpression;

public class SymCastExpressionImpl extends SymExpressionImpl implements SymCastExpression {

	protected SymCastExpressionImpl(CType data_type, SymExpression operand) throws IllegalArgumentException {
		super(data_type);
		this.add_child((SymNodeImpl) operand);
	}

	@Override
	public SymExpression get_operand() { return (SymExpression) this.get_child(0); }
	
	@Override
	public String toString() {
		return "<" + this.get_child(0).toString() + ">";
	}
	
}
