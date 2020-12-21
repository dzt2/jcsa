package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymFieldExpression extends SymExpression {

	protected SymFieldExpression(CType data_type) throws IllegalArgumentException {
		super(data_type);
	}
	
	public SymExpression get_body() {
		return (SymExpression) this.get_child(0);
	}
	
	public SymField get_field() {
		return (SymField) this.get_child(1);
	}
	
	@Override
	protected SymNode construct() throws Exception {
		return new SymFieldExpression(this.get_data_type());
	}
	
	/**
	 * @param data_type
	 * @param body
	 * @param field
	 * @return field expression := body.field
	 * @throws Exception
	 */
	protected static SymFieldExpression create(CType data_type, SymExpression body, SymField field) throws Exception {
		SymFieldExpression expression = new SymFieldExpression(data_type);
		expression.add_child(body);
		expression.add_child(field);
		return expression;
	}
	
}
