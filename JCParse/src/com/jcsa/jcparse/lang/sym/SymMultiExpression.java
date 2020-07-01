package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * 	multi_expression	|--	{+, *, &, |, ^, &&, ||}
 * 	@author yukimula
 *
 */
public class SymMultiExpression extends SymExpression {
	
	/**
	 * @param data_type
	 * @param operator	{+, *, &, |, ^, &&, ||}
	 */
	protected SymMultiExpression(CType data_type, COperator operator) {
		super(data_type, operator);
	}
	
	/**
	 * @return unary operator of the expression
	 */
	public COperator get_operator() { return (COperator) this.get_token(); }
	/**
	 * @param k 
	 * @return the kth operand under the multiple expression
	 * @throws IndexOutOfBoundsException
	 */
	public SymExpression get_operand(int k) throws IndexOutOfBoundsException { 
		return (SymExpression) this.get_child(k); 
	}

	/**
	 * @return number of operands in this expression
	 */
	public int number_of_operands() { return this.number_of_children(); }
	
	@Override
	protected SymNode clone_self() {
		return new SymMultiExpression(this.get_data_type(), this.get_operator());
	}
	

	@Override
	protected String generate_code(boolean ast_code) throws Exception {
		COperator operator = this.get_operator();
		String operator_code;
		switch(operator) {
		case arith_add:	operator_code = " + ";	break;
		case arith_mul:	operator_code = " * ";	break;
		case bit_and:	operator_code = " & ";	break;
		case bit_or:	operator_code = " | ";	break;
		case bit_xor:	operator_code = " ^ ";	break;
		case logic_and:	operator_code = " && ";	break;
		case logic_or:	operator_code = " || ";	break;
		default: throw new IllegalArgumentException("Invalid: " + operator);
		}
		
		StringBuilder buffer = new StringBuilder();
		for(int k = 0; k < this.number_of_operands(); k++) {
			buffer.append("(").append(this.get_operand(k).generate_code(ast_code)).append(")");
			if(k < this.number_of_operands() - 1) { buffer.append(operator_code); }
		}
		return buffer.toString();
	}
	
}
