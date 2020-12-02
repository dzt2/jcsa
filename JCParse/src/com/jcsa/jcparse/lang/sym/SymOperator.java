package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.lexical.COperator;

/**	
 * 	operator |-- {operator: COperator}
 * 	@author yukimula
 *	
 */
public class SymOperator extends SymUnit {
	
	/* definition */
	private COperator operator;
	private SymOperator(COperator operator) throws IllegalArgumentException {
		if(operator == null)
			throw new IllegalArgumentException("Invalid operator: null");
		else
			this.operator = operator;
	}
	
	/**
	 * @return the operator of the node
	 */
	public COperator get_operator() { return this.operator; }
	
	@Override
	protected SymNode construct() throws Exception {
		return new SymOperator(this.operator);
	}

	@Override
	public String generate_code() throws Exception {
		switch(operator) {
		case negative:		return "-";
		case bit_not:		return "~";
		case logic_not:		return "!";
		case address_of:	return "&";
		case dereference:	return "*";
		case assign:		return "";		/* no operator for type-casting */
		case arith_add:		return "+";
		case arith_sub:		return "-";
		case arith_mul:		return "*";
		case arith_div:		return "/";
		case arith_mod:		return "%";
		case bit_and:		return "&";
		case bit_or:		return "|";
		case bit_xor:		return "^";
		case left_shift:	return "<<";
		case righ_shift:	return ">>";
		case logic_and:		return "&&";
		case logic_or:		return "||";
		case greater_tn:	return ">";
		case greater_eq:	return ">=";
		case smaller_tn:	return "<";
		case smaller_eq:	return "<=";
		case equal_with:	return "==";
		case not_equals:	return "!=";
		default: throw new IllegalArgumentException("Invalid operator: " + operator);
		}
	}
	
	/**
	 * @param operator
	 * @return symbolic node to represent the operator
	 * @throws Exception
	 */
	protected static SymOperator create(COperator operator) throws Exception {
		return new SymOperator(operator);
	}
	
}
