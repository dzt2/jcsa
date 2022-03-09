package com.jcsa.jcparse.parse.symbolic;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * 	<code>
 * 	unary_expr	-->	{neg, rsv, not, inc, dec, adr, ref} expression
 * 	</code>
 * 	@author yukimula
 *
 */
public class SymbolUnaryExpression extends SymbolCompositeExpression {
	
	/**
	 * It creates a unary expression in symbolic node
	 * @param type
	 * @throws Exception
	 */
	private SymbolUnaryExpression(CType type) throws Exception {
		super(SymbolClass.unary_expression, type);
	}
	
	/**
	 * @return the unary operand in the expression
	 */
	public SymbolExpression get_operand() { return (SymbolExpression) this.get_child(1); }

	@Override
	protected SymbolNode new_one() throws Exception { return new SymbolUnaryExpression(this.get_data_type()); }

	@Override
	protected String generate_code(boolean simplified) throws Exception {
		String operand = this.get_operand().generate_code(simplified);
		if(!this.get_operand().is_leaf()) operand = "(" + operand + ")"; 
		switch(this.get_coperator()) {
		case negative:		return "-" + operand;
		case bit_not:		return "~" + operand;
		case logic_not:		return "!" + operand;
		case increment:		return operand + "++";
		case decrement:		return operand + "--";
		case address_of:	return "&" + operand;
		case dereference:	return "*" + operand;
		default:	throw new IllegalArgumentException("Unsupport: " + this.get_coperator());
		}
	}

	@Override
	protected boolean is_refer_type() { 
		switch(this.get_coperator()) {
		case dereference:	return true;
		default:			return false;
		}
	}

	@Override
	protected boolean is_side_affected() {
		switch(this.get_coperator()) {
		case increment:
		case decrement:	return true;
		default:		return false;
		}
	}
	
	/**
	 * @param type		the data type of the expression's output value
	 * @param operator	{neg, rsv, not, inc, dec, adr, ref}
	 * @param operand	the unary operand to be evaluated under this expression
	 * @return			unary_expression --> operator operand
	 * @throws Exception
	 */
	protected static SymbolUnaryExpression create(CType type, COperator operator, SymbolExpression operand) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(operator == null) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else if(operand == null) {
			throw new IllegalArgumentException("Invalid operand: null");
		}
		else {
			SymbolUnaryExpression expression;
			expression = new SymbolUnaryExpression(type);
			switch(operator) {
			case negative:
			case bit_not:
			case logic_not:
			case dereference:	
			{
				break;
			}
			case increment:	
			case decrement:	
			case address_of:
			{
				if(!operand.is_reference()) {
					throw new IllegalArgumentException("Not a reference: " + operand);
				}
				break;
			}
			default:	throw new IllegalArgumentException("Unsupport: " + operator);
			}
			expression.add_child(SymbolOperator.create(operator));
			expression.add_child(operand); return expression;
		}
	}

}
