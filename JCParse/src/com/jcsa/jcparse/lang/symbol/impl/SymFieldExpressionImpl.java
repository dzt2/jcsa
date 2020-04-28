package com.jcsa.jcparse.lang.symbol.impl;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.symbol.SymExpression;
import com.jcsa.jcparse.lang.symbol.SymField;
import com.jcsa.jcparse.lang.symbol.SymFieldExpression;

public class SymFieldExpressionImpl extends SymExpressionImpl implements SymFieldExpression {

	protected SymFieldExpressionImpl(CType data_type, SymExpression body, 
			String field) throws IllegalArgumentException {
		super(data_type);
		this.add_child((SymNodeImpl) body);
		this.add_child(new SymFieldImpl(field));
	}

	@Override
	public SymExpression get_body() { return (SymExpression) this.get_child(0); }
	@Override
	public SymField get_field() { return (SymField) this.get_child(1); }
	
	@Override
	public String toString() {
		return this.get_body().toString() + "." + this.get_field().toString();
	}
	
}
