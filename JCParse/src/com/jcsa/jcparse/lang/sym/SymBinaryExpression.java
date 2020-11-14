package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymBinaryExpression extends SymExpression {

	private SymBinaryExpression(CType data_type) throws IllegalArgumentException {
		super(data_type);
	}
	
	/**
	 * @return {+, -, *, /, %, &, |, ^, <<, >>, &&, ||, <, <=, >, >=, ==, !=}
	 */
	public SymOperator get_operator() { return (SymOperator) this.get_child(0); }
	
	/**
	 * @return left-operand
	 */
	public SymExpression get_loperand() { return (SymExpression) this.get_child(1); }
	
	/**
	 * @return right-operand
	 */
	public SymExpression get_roperand() { return (SymExpression) this.get_child(2); }

	@Override
	protected SymNode construct() throws Exception {
		return new SymBinaryExpression(this.get_data_type());
	}

	@Override
	public String generate_code() throws Exception {
		return "(" + this.get_loperand().generate_code() + ") "
				+ this.get_operator().generate_code() + " ("
				+ this.get_roperand().generate_code() + ")";
	}
	
	/**
	 * @param data_type
	 * @param operator
	 * @param loperand
	 * @param roperand
	 * @return symbolic binary expression := {type; (operator, loperand, roperand) }
	 * @throws Exception
	 */
	protected static SymBinaryExpression create(CType data_type, SymOperator operator, 
			SymExpression loperand, SymExpression roperand) throws Exception {
		SymBinaryExpression expression = new SymBinaryExpression(data_type);
		expression.add_child(operator);
		expression.add_child(loperand);
		expression.add_child(roperand);
		return expression;
	}
	
}
