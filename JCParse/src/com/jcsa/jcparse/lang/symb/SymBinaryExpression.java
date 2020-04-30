package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * 	BinaryExpression
 * 	|--	{+, -, *, /, %}
 * 	|--	{&, |, ^, <<, >>}
 * 	|--	{&&, ||}
 * 	|--	{<, <=, >, >=, !=, ==}
 * @author yukimula
 *
 */
public class SymBinaryExpression extends SymExpression {
	
	/* attribute */
	/** operator to interpret **/
	private COperator operator;
	
	/* constructor */
	/**
	 * create a binary expression for computation
	 * @param data_type
	 * @param operator
	 * @param loperand
	 * @param roperand
	 * @throws IllegalArgumentException
	 */
	protected SymBinaryExpression(CType data_type, COperator operator, 
			SymExpression loperand, SymExpression roperand) throws IllegalArgumentException {
		super(data_type);
		switch(operator) {
		case arith_sub:
		case arith_div:
		case arith_mod:
		case left_shift:
		case righ_shift:
		case greater_tn:
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case not_equals:
		case equal_with:	
			this.add_child(loperand); 
			this.add_child(roperand); 
			this.operator = operator; 
			break;
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
	 * get the left-operand
	 * @return
	 */
	public SymExpression get_loperand() { return (SymExpression) this.get_child(0); }
	/**
	 * get the right-operand
	 * @return
	 */
	public SymExpression get_roperand() { return (SymExpression) this.get_child(1); }
	
	/* setters */
	/**
	 * set the left-operand
	 * @param loperand
	 * @throws IllegalArgumentException
	 */
	public void set_loperand(SymExpression loperand) throws IllegalArgumentException {
		this.set_child(0, loperand);
	}
	/**
	 * set the right-operand
	 * @param roperand
	 * @throws IllegalArgumentException
	 */
	public void set_roperand(SymExpression roperand) throws IllegalArgumentException {
		this.set_child(1, roperand);
	}
	
	private String operator_name() {
		switch(operator) {
		case arith_sub:		return "-";
		case arith_div:		return "/";
		case arith_mod:		return "%";
		case left_shift:	return "<<";
		case righ_shift:	return ">>";
		case greater_tn:	return ">";
		case greater_eq:	return ">=";
		case smaller_tn:	return "<";
		case smaller_eq:	return "<=";
		case not_equals:	return "!=";
		case equal_with:	return "==";
		default: return null;
		}
	}
	@Override
	public String toString() {
		return "(" + this.get_loperand() + " " + this.operator_name() + " " + this.get_roperand() + ")";
	}
	
}
