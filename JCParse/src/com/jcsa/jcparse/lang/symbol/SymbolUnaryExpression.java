package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * 	<code> {neg, rsv, not, adr, der} </code>
 * 	@author yukimula
 *
 */
public class SymbolUnaryExpression extends SymbolCompositeExpression {
	
	/**
	 * It creates an isolated unary expression with specified type
	 * @param data_type
	 * @throws IllegalArgumentException
	 */
	private SymbolUnaryExpression(CType data_type) throws IllegalArgumentException {
		super(SymbolClass.unary_expression, data_type);
	}
	
	/**
	 * @param operator	[neg, rsv, not, adr, der]
	 * @param operand	the unary operand used in
	 * @return			expr --> oprt expr
	 * @throws IllegalArgumentException
	 */
	protected static SymbolUnaryExpression create(CType type, COperator operator, 
			SymbolExpression operand) throws IllegalArgumentException {
		if(operator == null) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else if(operand == null) {
			throw new IllegalArgumentException("Invalid operand: null");
		}
		else {
			switch(operator) {
			case negative:
			case bit_not:
			case logic_not:
			case address_of:
			case dereference:	break;
			default:	throw new IllegalArgumentException("Invalid: " + operator);
			}
			SymbolUnaryExpression expression = new SymbolUnaryExpression(type);
			expression.add_child(SymbolOperator.create(operator));
			expression.add_child(operand); return expression;
		}
	}
	
	/**
	 * @return the unary operand
	 */
	public SymbolExpression get_operand() { return this.get_operand(0); }

	@Override
	protected SymbolNode new_one() throws Exception {
		return new SymbolUnaryExpression(this.get_data_type());
	}

	@Override
	protected String generate_code(boolean simplified) throws Exception {
		String operator = this.get_operator().generate_code(simplified);
		String operand = this.get_operand().generate_code(simplified);
		if(this.get_operand().is_leaf()) operand = "(" + operand + ")";
		return operator + operand;
	}

	@Override
	protected boolean is_refer_type() {
		switch(this.get_coperator()) {
		case dereference:	return true;
		default:			return false;
		}
	}
	
	@Override
	protected boolean is_side_affected() { return false; } 
	
}
