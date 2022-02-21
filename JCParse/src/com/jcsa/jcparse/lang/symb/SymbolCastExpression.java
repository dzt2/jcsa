package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymbolCastExpression extends SymbolExpression {

	private SymbolCastExpression(CType type) throws Exception {
		super(SymbolClass.cast_expression, type);
	}
	
	/**
	 * @return the type to cast the expression's operand
	 */
	public SymbolType get_cast_type() { return (SymbolType) this.get_child(0); }
	
	/**
	 * @return the operand to be casted to specified type
	 */
	public SymbolExpression get_casted_operand() { return (SymbolExpression) this.get_child(1); }

	@Override
	protected SymbolNode construct_copy() throws Exception {
		return new SymbolCastExpression(this.get_data_type());
	}

	@Override
	protected String get_code(boolean simplified) throws Exception {
		String cast_type = this.get_cast_type().get_code(simplified);
		String operand = this.get_casted_operand().get_code(simplified);
		if(!this.get_casted_operand().is_leaf()) {
			operand = "(" + operand + ")";
		}
		return "(" + cast_type + ") " + operand;
	}

	@Override
	protected boolean is_refer_type() {
		return false;
	}

	@Override
	protected boolean is_side_affected() {
		return false;
	}
	
	/**
	 * @param type
	 * @param operand
	 * @return
	 * @throws Exception
	 */
	protected static SymbolCastExpression create(SymbolType type, SymbolExpression operand) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(operand == null) {
			throw new IllegalArgumentException("Invalid operand: null");
		}
		else {
			SymbolCastExpression expression = new SymbolCastExpression(type.get_type());
			expression.add_child(type);
			expression.add_child(operand);
			return expression;
		}
	}
	
}
