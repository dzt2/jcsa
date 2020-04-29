package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * 	UnaryExpression
 * 	|--	{+, -, !, ~, &, *, cast<assign>} expression
 * @author yukimula
 *
 */
public class SymUnaryExpression extends SymExpression {
	
	/** the operator to run the expression **/
	private COperator operator;
	
	/**
	 * (operator operand)
	 * @param data_type
	 * @param operator
	 * @param operand
	 * @throws IllegalArgumentException
	 */
	protected SymUnaryExpression(CType data_type, COperator operator, 
			SymExpression operand) throws IllegalArgumentException {
		super(data_type);
		switch(operator) {
		case positive:
		case negative:
		case bit_not:
		case logic_not:
		case address_of:
		case dereference:
		case assign:		this.add_child(operand); this.operator = operator; break;
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
	}
	
	/* getters */
	/**
	 * get the operator of the unary expression
	 * @return
	 */
	public COperator get_operator() { return this.operator; }
	
	/**
	 * get the operand as expression under this expression
	 * @return
	 */
	public SymExpression get_operand() { return (SymExpression) this.get_child(0); }
	
	/**
	 * set the operand under the unary expression
	 * @param operand
	 * @throws IllegalArgumentException
	 */
	public void set_operand(SymExpression operand) throws IllegalArgumentException {
		this.set_child(0, operand);
	}
	
	private String operator_name() {
		switch(operator) {
		case positive:		return "+";
		case negative:		return "-";
		case bit_not:		return "~";
		case logic_not:		return "!";
		case address_of:	return "&";
		case dereference:	return "*";
		case assign:		return "cast";
		default: return null;
		}
	}
	@Override
	public String toString() {
		return "(" + this.operator_name() + " " + this.get_operand().toString() + ")";
	}
	
}
