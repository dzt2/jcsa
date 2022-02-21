package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * <code>
 * 	SymbolUnaryExpression		{unary_expr |--> (+, -, ~, !, &, *, ++, --, p++, p--) expr}		<br>
 * </code>
 * 
 * @author yukimula
 *
 */
public class SymbolUnaryExpression extends SymbolExpression {

	protected SymbolUnaryExpression(CType type) throws Exception {
		super(SymbolClass.unary_expression, type);
	}
	
	/**
	 * @return (+, -, ~, !, &, *, ++, --, p++, p--)
	 */
	public SymbolOperator get_operator() { 
		return (SymbolOperator) this.get_child(0); 
	}
	
	/**
	 * @return the unary operand in the expression
	 */
	public SymbolExpression get_operand() {
		return (SymbolExpression) this.get_child(1);
	}

	@Override
	protected SymbolNode construct_copy() throws Exception {
		return new SymbolUnaryExpression(this.get_data_type());
	}

	@Override
	protected String get_code(boolean simplified) throws Exception {
		String operand = this.get_operand().get_code(simplified);
		if(!this.get_operand().is_leaf()) {
			operand = "(" + operand + ")";
		}
		
		switch(this.get_operator().get_operator()) {
		case positive:			return operand;
		case negative:			return "-" + operand;
		case bit_not:			return "~" + operand;
		case logic_not:			return "!" + operand;
		case address_of:		return "&" + operand;
		case dereference:		return "*" + operand;
		case increment:			return "++" + operand;
		case decrement:			return "--" + operand;
		case arith_add_assign:	return operand + "++";
		case arith_sub_assign:	return operand + "--";
		default:	throw new IllegalArgumentException("Unsupport: " + this.get_operator());
		}
	}

	@Override
	protected boolean is_refer_type() {
		switch(this.get_operator().get_operator()) {
		case dereference:	return true;
		default:			return false;
		}
	}

	@Override
	protected boolean is_side_affected() {
		switch(this.get_operator().get_operator()) {
		case increment:
		case decrement:
		case arith_add_assign:
		case arith_sub_assign:	return true;
		default:				return false;
		}
	}
	
	/**
	 * @param type
	 * @param operator
	 * @param operand
	 * @return	{unary_expr |--> (+, -, ~, !, &, *, ++, --, p++, p--) expr}
	 * @throws Exception
	 */
	protected static SymbolUnaryExpression create(CType type, 
			COperator operator, SymbolExpression operand) throws Exception {
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
			switch(operator) {
			case positive:
			case negative:
			case bit_not:
			case logic_not:
			case address_of:
			case dereference:
			case increment:
			case decrement:
			case arith_add_assign:
			case arith_sub_assign:	break;
			default:	throw new IllegalArgumentException("Invalid operator");
			}
			
			SymbolUnaryExpression expression = new SymbolUnaryExpression(type);
			expression.add_child(SymbolOperator.create(operator));
			expression.add_child(operand);
			return expression;
		}
	}
	
}
