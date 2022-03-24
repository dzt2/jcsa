package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymbolFieldExpression extends SymbolSpecialExpression {
	
	/**
	 * It creates a field-expression with given body and field
	 * @param type
	 * @returns
	 * @throws IllegalArgumentException
	 */
	private SymbolFieldExpression(CType data_type) throws IllegalArgumentException {
		super(SymbolClass.field_expression, data_type);
	}
	
	/**
	 * It creates a field-expression with given body and field
	 * @param type
	 * @param body
	 * @param field
	 * @returns
	 * @throws IllegalArgumentException
	 */
	protected static SymbolFieldExpression create(CType type, SymbolExpression 
			body, SymbolField field) throws IllegalArgumentException {
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
			expression.add_child(body); expression.add_child(field); 
			return expression;
		}
	}
	
	/**
	 * @return the body of the expression
	 */
	public SymbolExpression get_body() { return (SymbolExpression) this.get_child(0); }
	
	/**
	 * @return the field to read the body
	 */
	public SymbolField get_field() { return (SymbolField) this.get_child(1); }

	@Override
	protected SymbolNode new_one() throws Exception {
		return new SymbolFieldExpression(this.get_data_type());
	}

	@Override
	protected String generate_code(boolean simplified) throws Exception {
		String body = this.get_body().generate_code(simplified);
		String field = this.get_field().generate_code(simplified);
		if(!this.get_body().is_leaf()) { body = "(" + body + ")"; }
		return body + "." + field;
	}

	@Override
	protected boolean is_refer_type() { return true; }

	@Override
	protected boolean is_side_affected() { return false; }
	
}
