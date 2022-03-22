package com.jcsa.jcparse.lang.symbol;

import java.util.Map;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymbolFieldExpression extends SymbolSpecialExpression {

	private SymbolFieldExpression(CType type) throws Exception {
		super(SymbolClass.field_expression, type);
	}
	
	/**
	 * @return the body of which field is derived
	 */
	public SymbolExpression get_body() { return (SymbolExpression) this.get_child(0); }
	
	/**
	 * @return the field to index the body expression
	 */
	public SymbolField get_field() { return (SymbolField) this.get_child(1); }
	
	/**
	 * @param type
	 * @param body
	 * @param field
	 * @return body.field
	 * @throws Exception
	 */
	protected static SymbolFieldExpression create(CType type, SymbolExpression body, SymbolField field) throws Exception {
		if(body == null) {
			throw new IllegalArgumentException("Invalid body: null");
		}
		else if(field == null) {
			throw new IllegalArgumentException("Invalid field: null");
		}
		else {
			SymbolFieldExpression expression = new SymbolFieldExpression(type);
			expression.add_child(body); expression.add_child(field); return expression;
		}
	}

	@Override
	protected SymbolNode new_one() throws Exception {
		return new SymbolFieldExpression(this.get_data_type());
	}

	@Override
	protected String generate_code(boolean simplified) throws Exception {
		String body = this.get_body().generate_code(simplified);
		if(!this.get_body().is_leaf()) {
			body = "(" + body + ")";
		}
		return body + "." + this.get_field().get_name();
	}

	@Override
	protected boolean is_refer_type() { return true; }

	@Override
	protected boolean is_side_affected() { return false; }

	@Override
	protected SymbolExpression symb_replace(Map<SymbolExpression, SymbolExpression> name_value_map) throws Exception {
		if(name_value_map.containsKey(this)) {
			return name_value_map.get(this);
		}
		else {
			CType data_type = this.get_data_type();
			SymbolExpression body = this.get_body();
			SymbolField field = this.get_field();
			body = body.symb_replace(name_value_map);
			return create(data_type, body, field);
		}
	}
	
}
