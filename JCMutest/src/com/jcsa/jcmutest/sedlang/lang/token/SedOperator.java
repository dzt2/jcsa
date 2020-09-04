package com.jcsa.jcmutest.sedlang.lang.token;

import com.jcsa.jcmutest.sedlang.lang.SedNode;
import com.jcsa.jcparse.lang.lexical.COperator;

public class SedOperator extends SedToken {
	
	private COperator operator;
	public SedOperator(COperator operator) throws IllegalArgumentException {
		if(operator == null)
			throw new IllegalArgumentException("Invalid operator: null");
		else 
			this.operator = operator;
	}
	
	/**
	 * @return the operator of this node
	 */
	public COperator get_operator() { return this.operator; }
	
	@Override
	public String generate_code() throws Exception {
		switch(this.operator) {
		case assign:			return "@c";
		case positive:			return "@p";
		case negative:			return "@n";
		case address_of:		return "@a";
		case dereference:		return "@d";
		case increment:			return "++";
		case decrement:			return "--";
		case arith_add:			return "+";
		case arith_sub:			return "-";
		case arith_mul:			return "*";
		case arith_div:			return "/";
		case arith_mod:			return "%";
		case bit_not:			return "~";
		case bit_and:			return "&";
		case bit_or:			return "|";
		case bit_xor:			return "^";
		case left_shift:		return "<<";
		case righ_shift:		return ">>";
		case logic_not:			return "!";
		case logic_and:			return "&&";
		case logic_or:			return "||";
		case greater_tn:		return ">";
		case greater_eq:		return ">=";
		case smaller_tn:		return "<";
		case smaller_eq:		return "<=";
		case equal_with:		return "==";
		case not_equals:		return "!=";
		case arith_add_assign:	return "+=";
		case arith_sub_assign:	return "-=";
		case arith_mul_assign:	return "*=";
		case arith_div_assign:	return "/=";
		case arith_mod_assign:	return "%=";
		case bit_and_assign:	return "&=";
		case bit_or_assign:		return "|=";
		case bit_xor_assign:	return "^=";
		case left_shift_assign:	return "<<=";
		case righ_shift_assign:	return ">>=";
		default: throw new IllegalArgumentException("Unsupport: " + operator);
		}
	}

	@Override
	protected SedNode construct() throws Exception {
		return new SedOperator(operator);
	}

}
