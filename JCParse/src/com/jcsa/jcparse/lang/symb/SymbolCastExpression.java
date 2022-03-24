package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * cast_expr --> type expression
 * @author yukimula
 *
 */
public class SymbolCastExpression extends SymbolSpecialExpression {
	
	/**
	 * It creates an isolated node of casting expression
	 * @param data_type
	 * @throws IllegalArgumentException
	 */
	private SymbolCastExpression(CType data_type) throws IllegalArgumentException {
		super(SymbolClass.cast_expression, data_type);
	}
	
	/**
	 * It creates a type-casting expression
	 * @param type		the type to cast
	 * @param operand	the operand being casted
	 * @return			cast_expr : type expression
	 * @throws IllegalArgumentException
	 */
	protected static SymbolCastExpression create(SymbolType type, 
			SymbolExpression operand) throws IllegalArgumentException {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(operand == null) {
			throw new IllegalArgumentException("Invalid operand: null");
		}
		else {
			SymbolCastExpression expression = new SymbolCastExpression(type.get_type());
			expression.add_child(type); expression.add_child(operand); return expression;
		}
	}
	
	/**
	 * @return the type to cast the operand's value
	 */
	public SymbolType get_cast_type() { return (SymbolType) this.get_child(0); }
	
	/** 
	 * @return the operand being casted in the expression
	 */
	public SymbolExpression get_operand() { return (SymbolExpression) this.get_child(1); }

	@Override
	protected SymbolNode new_one() throws Exception {
		return new SymbolCastExpression(this.get_data_type());
	}

	@Override
	protected String generate_code(boolean simplified) throws Exception {
		String type = "(" + this.get_cast_type().generate_code(simplified) + ")";
		String operand = this.get_operand().generate_code(simplified);
		return type + " " + operand;
	}

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return false; }
	
}
