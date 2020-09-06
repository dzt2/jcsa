package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.ctype.CType;

public class SymBinaryExpression extends SymExpression {

	protected SymBinaryExpression(CType data_type) throws IllegalArgumentException {
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
	
}
