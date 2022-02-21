package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymbolFieldExpression extends SymbolExpression {

	private SymbolFieldExpression(CType type) throws Exception {
		super(SymbolClass.field_expression, type);
	}
	
	/**
	 * @return the body of the field-expression
	 */
	public SymbolExpression get_body() { return (SymbolExpression) this.get_child(0); }
	
	/**
	 * @return the field of the field-expression
	 */
	public SymbolField get_field() { return (SymbolField) this.get_child(1); }

	@Override
	protected SymbolNode construct_copy() throws Exception {
		return new SymbolFieldExpression(this.get_data_type());
	}

	@Override
	protected String get_code(boolean simplified) throws Exception {
		String body = this.get_body().get_code(simplified);
		String field = this.get_field().get_code(simplified);
		if(!this.get_body().is_leaf()) {
			body = "(" + body + ")";
		}
		return body + "." + field;
	}

	@Override
	protected boolean is_refer_type() {
		return true;
	}

	@Override
	protected boolean is_side_affected() {
		return false;
	}
	
	/**
	 * @param type
	 * @param body
	 * @param field
	 * @return
	 * @throws Exception
	 */
	protected static SymbolFieldExpression create(CType type, SymbolExpression body, SymbolField field) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(body == null) {
			throw new IllegalArgumentException("Invalid body: null");
		}
		else if(field == null) {
			throw new IllegalArgumentException("Invalid field: null");
		}
		else {
			SymbolFieldExpression expression = new SymbolFieldExpression(type);
			expression.add_child(body);
			expression.add_child(field);
			return expression;
		}
	}

}
