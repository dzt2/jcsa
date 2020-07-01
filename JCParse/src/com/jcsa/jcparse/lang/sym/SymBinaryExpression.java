package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * 	binary_expression	|--	{-, /, %, <<, >>, <, <=, >, >=, ==, !=}
 * 	@author yukimula
 */
public class SymBinaryExpression extends SymExpression {
	
	/**
	 * @param data_type
	 * @param operator	{-, /, %, <<, >>, <, <=, >, >=, ==, !=}
	 */
	protected SymBinaryExpression(CType data_type, COperator operator) {
		super(data_type, operator);
	}
	
	/**
	 * @return unary operator of the expression
	 */
	public COperator get_operator() { return (COperator) this.get_token(); }
	/**
	 * @return left operand
	 */
	public SymExpression get_loperand() { return (SymExpression) this.get_child(0); }
	/**
	 * @return right-operand
	 */
	public SymExpression get_roperand() { return (SymExpression) this.get_child(1); }

	@Override
	protected SymNode clone_self() {
		return new SymBinaryExpression(this.get_data_type(), this.get_operator());
	}

	@Override
	protected String generate_code(boolean ast_code) throws Exception {
		COperator operator = this.get_operator();
		String loperand = "(" + this.get_loperand().generate_code(ast_code) + ")";
		String roperand = "(" + this.get_roperand().generate_code(ast_code) + ")";
		switch(operator) {
		case arith_sub:		return loperand + " - " + roperand;
		case arith_div:		return loperand + " / " + roperand;
		case arith_mod:		return loperand + " % " + roperand;
		case left_shift:	return loperand + " << " + roperand;
		case righ_shift:	return loperand + " >> " + roperand;
		case greater_tn:	return loperand + " > " + roperand;
		case greater_eq:	return loperand + " >= " + roperand;
		case smaller_tn:	return loperand + " < " + roperand;
		case smaller_eq:	return loperand + " <= " + roperand;
		case equal_with:	return loperand + " == " + roperand;
		case not_equals:	return loperand + " != " + roperand;
		default:		throw new IllegalArgumentException("Invalid: " + operator);
		}
	}
	
}
