package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * SymBinaryExpression		{operator: -, /, %, <<, >>, <, <=, >, >=, ==, !=}
 * @author yukimula
 *
 */
public class SymBinaryExpression extends SymExpression {
	
	private COperator operator;
	protected SymBinaryExpression(CType data_type, COperator operator) {
		super(data_type);
		this.operator = operator;
	}
	
	/**
	 * @return {operator: -, /, %, <<, >>, <, <=, >, >=, ==, !=}
	 */
	public COperator get_operator() {
		return this.operator;
	}
	/**
	 * @return left-operand
	 */
	public SymExpression get_loperand() {
		return (SymExpression) this.get_child(0);
	}
	/**
	 * @return right-operand
	 */
	public SymExpression get_roperand() {
		return (SymExpression) this.get_child(1);
	}

	@Override
	protected SymNode new_self() {
		return new SymBinaryExpression(this.get_data_type(), this.operator);
	}

	@Override
	protected String generate_code(boolean ast_style) throws Exception {
		String loperand = "(" + this.get_loperand().generate_code(ast_style) + ")";
		String roperand = "(" + this.get_roperand().generate_code(ast_style) + ")";
		String operator;
		switch(this.operator) {
		case arith_sub:		operator = " - ";	break;
		case arith_div:		operator = " / ";	break;
		case arith_mod:		operator = " % ";	break;
		case left_shift:	operator = " << ";	break;
		case righ_shift:	operator = " >> ";	break;
		case greater_tn:	operator = " > ";	break;
		case greater_eq:	operator = " >= ";	break;
		case smaller_tn:	operator = " < ";	break;
		case smaller_eq:	operator = " <= ";	break;
		case equal_with:	operator = " == ";	break;
		case not_equals:	operator = " != ";	break;
		default: throw new IllegalArgumentException("Invalid operator.");
		}
		return loperand + operator + roperand;
	}

}
