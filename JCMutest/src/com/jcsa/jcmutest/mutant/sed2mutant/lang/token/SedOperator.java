package com.jcsa.jcmutest.mutant.sed2mutant.lang.token;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.SedNode;
import com.jcsa.jcparse.lang.lexical.COperator;

public class SedOperator extends SedToken {

	private COperator operator;
	public SedOperator(COperator operator) {
		super(null);
		this.operator = operator;
	}
	
	/**
	 * @return the operator 
	 */
	public COperator get_operator() { return this.operator; }

	@Override
	protected SedNode clone_self() {
		return new SedOperator(this.operator);
	}

	@Override
	public String generate_code() throws Exception {
		switch(this.operator) {
		case negative:		return "-";
		case bit_not:		return "~";
		case logic_not:		return "!";
		case address_of:	return "&";
		case dereference:	return "*";
		case assign:		return "@";
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
		case greater_tn:	return ">";
		case greater_eq:	return ">=";
		case smaller_tn:	return "<";
		case smaller_eq:	return "<=";
		case equal_with:	return "==";
		case not_equals:	return "!=";
		default: throw new IllegalArgumentException("Unknown operator: " + this.operator);
		}
	}
	
}	
