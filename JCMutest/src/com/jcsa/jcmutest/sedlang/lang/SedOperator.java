package com.jcsa.jcmutest.sedlang.lang;

import com.jcsa.jcmutest.sedlang.SedNode;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * Operator |-- [positive, negative, bit_not, logic_not, address_of, dereference, increment, decrement]
 * 			|--	[+, -, *, /, %, &, |, ^, <<, >>, <, <=, >, >=, ==, !=, &&, ||, assign(cast), ]
 * @author yukimula
 *
 */
public class SedOperator extends SedNode {
	
	/* definition */
	/** the operator that this node defines **/
	private COperator operator;
	protected SedOperator(CirNode source, COperator operator) {
		super(source);
		this.operator = operator;
	}
	/**
	 * @return the operator that this node defines
	 */
	public COperator get_operator() { return this.operator; }
	
	/* implementation */
	@Override
	protected SedNode copy_self() {
		return new SedOperator(this.get_source(), this.operator);
	}
	@Override
	public String generate_code() throws Exception {
		switch(this.operator) {
		case positive:		return "";
		case negative:		return "-";
		case bit_not:		return "~";
		case logic_not:		return "!";
		case address_of:	return "&";
		case dereference:	return "*";
		case increment:		return "++";
		case decrement:		return "--";
		case assign:		return "#cast";
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
	
}
