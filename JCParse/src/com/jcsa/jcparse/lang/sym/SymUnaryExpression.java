package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * unary_expression	|-- {+, -, ~, !, &, *, assign}
 * @author yukimula
 *
 */
public class SymUnaryExpression extends SymExpression {
	
	/**
	 * @param data_type
	 * @param operator {positive, negative, bit_not, logic_not, address_of, dereference, assign}
	 */
	protected SymUnaryExpression(CType data_type, COperator operator) {
		super(data_type, operator);
	}
	
	/**
	 * @return unary operator of the expression
	 */
	public COperator get_operator() { return (COperator) this.get_token(); }
	
	/**
	 * @return the unique operand in this expression
	 */
	public SymExpression get_operand() { return (SymExpression) this.get_child(0); }
	
	@Override
	protected SymNode clone_self() {
		return new SymUnaryExpression(this.get_data_type(), this.get_operator());
	}
	
	@Override
	protected String generate_code(boolean ast_code) throws Exception {
		COperator operator = this.get_operator();
		String operand = this.get_operand().generate_code(ast_code);
		switch(operator) {
		case positive:		return operand;
		case negative:		return "-(" + operand + ")";
		case bit_not:		return "~(" + operand + ")";
		case logic_not:		return "!(" + operand + ")";
		case address_of:	return "&(" + operand + ")";
		case dereference:	return "*(" + operand + ")";
		case assign:		return "(" + this.get_data_type().generate_code() + ") (" + operand + ")";
		default:			throw new IllegalArgumentException("Invalid operator: " + operator);
		}
	}
	
}
