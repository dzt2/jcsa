package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * operator |-- {+, -, *, /, %, &, |, ^, <<, >>, &&, ||, -, ~, !, &, *, =, <, <=, >, >=, ==, !=}
 * @author yukimula
 *
 */
public class SymbolOperator extends SymbolUnit {

	/** the operator used to define this node **/
	private COperator operator;

	/**
	 * {+, -, *, /, %, &, |, ^, <<, >>, &&, ||, -, ~, !, &, *, =}
	 * @param operator
	 * @throws IllegalArgumentException
	 */
	private SymbolOperator(COperator operator) throws IllegalArgumentException {
		if(operator == null)
			throw new IllegalArgumentException("Invalid operator as null");
		else {
			switch(operator) {
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
			case greater_tn:
			case greater_eq:
			case smaller_tn:
			case smaller_eq:
			case equal_with:
			case not_equals:
			case logic_and:
			case logic_or:
			case negative:
			case bit_not:
			case logic_not:
			case address_of:
			case dereference:
			case assign:		this.operator = operator;	break;
			default: throw new IllegalArgumentException("Invalid: " + operator);
			}
		}
	}

	/**
	 * @return {+, -, *, /, %, &, |, ^, <<, >>, &&, ||, -, ~, !, &, *, =}
	 */
	public COperator get_operator() { return this.operator; }

	@Override
	protected SymbolNode construct() throws Exception {
		return new SymbolOperator(this.operator);
	}

	/**
	 * @param operator
	 * @return operator |-- {+, -, *, /, %, &, |, ^, <<, >>, &&, ||, -, ~, !, &, *, =, <, <=, >, >=, ==, !=}
	 * @throws Exception
	 */
	protected static SymbolOperator create(COperator operator) throws Exception {
		return new SymbolOperator(operator);
	}

}
