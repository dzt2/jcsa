package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * field_expression |== expression . field
 * @author yukimula
 *
 */
public class SymbolFieldExpression extends SymbolExpression {

	private SymbolFieldExpression(CType data_type) throws IllegalArgumentException {
		super(data_type);
	}

	@Override
	protected SymbolNode construct() throws Exception {
		return new SymbolFieldExpression(this.get_data_type());
	}
	
	public SymbolExpression get_body() { return (SymbolExpression) this.get_child(0); }
	public SymbolField get_field() { return (SymbolField) this.get_child(1); }
	
	/**
	 * @param data_type
	 * @param body
	 * @param field
	 * @return field_expression |== expression . field
	 * @throws Exception
	 */
	protected static SymbolFieldExpression create(CType data_type, 
			SymbolExpression body, SymbolField field) throws Exception {
		SymbolFieldExpression expression = new SymbolFieldExpression(data_type);
		expression.add_child(body); expression.add_child(field); return expression;
	}
	
}
