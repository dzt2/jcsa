package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * SymMultiExpression		{operator: +, *, &, |, ^, &&, ||}
 * @author yukimula
 *
 */
public class SymMultiExpression extends SymExpression {

	private COperator operator;
	protected SymMultiExpression(CType data_type, COperator operator) {
		super(data_type);
		this.operator = operator;
	}
	
	/**
	 * @return {operator: +, *, &, |, ^, &&, ||}
	 */
	public COperator get_operator() {
		return this.operator;
	}
	/**
	 * @return number of operands in this expression
	 */
	public int number_of_operands() { return this.number_of_children(); }
	/**
	 * @param k
	 * @return the kth operand in the expression
	 * @throws IndexOutOfBoundsException
	 */
	public SymExpression get_operand(int k) throws IndexOutOfBoundsException {
		return (SymExpression) this.get_child(k);
	}

	@Override
	protected SymNode new_self() {
		return new SymMultiExpression(this.get_data_type(), this.operator);
	}

	@Override
	protected String generate_code(boolean ast_style) throws Exception {
		String operator;
		switch(this.operator) {
		case arith_add:		operator = " + ";	break;
		case arith_mul:		operator = " * ";	break;
		case bit_and:		operator = " & ";	break;
		case bit_or:		operator = " | ";	break;
		case bit_xor:		operator = " ^ ";	break;
		case logic_and:		operator = " && ";	break;
		case logic_or:		operator = " || ";	break;
		default: throw new IllegalArgumentException("Invalid operator.");
		}
		
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < this.number_of_operands(); k++) {
			String operand = this.get_operand(k).generate_code(ast_style);
			buffer.append(operand);
			if(k < this.number_of_operands() - 1) {
				buffer.append(operator);
			}
		}
		return buffer.toString();
	}
	

}
