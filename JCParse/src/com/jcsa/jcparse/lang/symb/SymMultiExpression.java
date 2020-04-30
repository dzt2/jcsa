package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

public class SymMultiExpression extends SymExpression {
	
	/* attribute */
	/** operator to interpret **/
	private COperator operator;
	
	/* constructor */
	/**
	 * create an expression with multiple operands (more than 1)
	 * @param data_type
	 * @param operator
	 * @throws IllegalArgumentException
	 */
	protected SymMultiExpression(CType data_type, COperator operator) throws IllegalArgumentException {
		super(data_type);
		switch(operator) {
		case arith_add:
		case arith_mul:
		case bit_and:
		case bit_or:
		case bit_xor:
		case logic_and:
		case logic_not:	this.operator = operator; break;
		default: throw new IllegalArgumentException("Invalid operator");
		}
	}
	
	/* getters */
	/**
	 * get the operator to interpret the expression
	 * @return
	 */
	public COperator get_operator() { return this.operator; }
	/**
	 * get the number of operands in the expression
	 * @return
	 */
	public int number_of_operands() { return this.number_of_children(); }
	/**
	 * get the kth operand in the expression
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public SymExpression get_operand(int k) throws IndexOutOfBoundsException {
		return (SymExpression) this.get_child(k);
	}
	
	/* setters */
	/**
	 * add an operand in the expression
	 * @param operand
	 * @throws IllegalArgumentException
	 */
	public void add_operand(SymExpression operand) throws IllegalArgumentException {
		this.add_child(operand);
	}
	
	private String operator_name() {
		switch(operator) {
		case arith_add:		return "+";
		case arith_mul:		return "*";
		case bit_and:		return "&";
		case bit_or:		return "|";
		case bit_xor:		return "^";
		case logic_and:		return "&&";
		case logic_or:		return "||";
		default: return null;
		}
	}
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("(");
		for(int k = 0; k < this.number_of_children(); k++) {
			SymExpression operand = this.get_operand(k);
			buffer.append(operand.toString());
			if(k < this.number_of_children() - 1) {
				buffer.append(" ");
				buffer.append(this.operator_name());
				buffer.append(" ");
			}
		}
		buffer.append(")");
		
		return buffer.toString();
	}
	
}
