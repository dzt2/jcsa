package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * SymbolBinaryExpression		{operator: +, -, *, /, %, &, |, ^, <<, >>, &&, ||, <, <=, >, >=, ==, !=}								
 * @author yukimula
 *
 */
public class SymbolBinaryExpression extends SymbolExpression {

	private SymbolBinaryExpression(CType data_type) throws IllegalArgumentException {
		super(data_type);
	}

	@Override
	protected SymbolNode construct() throws Exception {
		return new SymbolBinaryExpression(this.get_data_type());
	}
	
	/**
	 * @return {operator: +, -, *, /, %, &, |, ^, <<, >>, &&, ||}
	 */
	public SymbolOperator get_operator() { return (SymbolOperator) this.get_child(0); }
	/**
	 * @return left-operand
	 */
	public SymbolExpression get_loperand() { return (SymbolExpression) this.get_child(1); }
	/**
	 * @return right-operand
	 */
	public SymbolExpression get_roperand() { return (SymbolExpression) this.get_child(2); }
	
	/**
	 * 
	 * @param data_type
	 * @param operator	{operator: +, -, *, /, %, &, |, ^, <<, >>, &&, ||, <, <=, >, >=, ==, !=}	
	 * @param loperand
	 * @param roperand
	 * @return 
	 * @throws Exception
	 */
	protected static SymbolBinaryExpression create(CType data_type, SymbolOperator operator,
			SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		switch(operator.get_operator()) {
		case arith_add:
		case arith_sub:
		case arith_mul:
		case arith_div:
		case arith_mod:
		case bit_and:
		case bit_or:
		case bit_xor:
		case left_shift:
		case righ_shift:
		case logic_and:
		case logic_or:
		case greater_tn:
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case equal_with:
		case not_equals:
		{
			SymbolBinaryExpression expression = new SymbolBinaryExpression(data_type);
			expression.add_child(operator);
			expression.add_child(loperand);
			expression.add_child(roperand);
			return expression;
		}
		default: throw new IllegalArgumentException(operator.get_operator().toString());
		}
	}
	
}
