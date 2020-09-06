package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * {-, ~, !, &, *, assign} expression
 * 
 * @author yukimula
 *
 */
public class SymUnaryExpression extends SymExpression {

	protected SymUnaryExpression(CType data_type) throws IllegalArgumentException {
		super(data_type);
	}
	
	/**
	 * @return unary operator
	 */
	public SymOperator get_operator() { return (SymOperator) this.get_child(0); }
	
	/**
	 * @return the unary operand
	 */
	public SymExpression get_operand() { return (SymExpression) this.get_child(1); }
	
	@Override
	protected SymNode construct() throws Exception {
		return new SymUnaryExpression(this.get_data_type());
	}

	@Override
	public String generate_code() throws Exception {
		return this.get_operator().generate_code() + "("
				+ this.get_operand().generate_code() + ")";
	}

}
