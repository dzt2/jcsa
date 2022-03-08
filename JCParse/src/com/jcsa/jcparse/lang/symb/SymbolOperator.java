package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * {+, -, *, /, %, &, |, ^, <<, >>, &&, ||, <, <=, >, >=, !=, ==, :=, +, -, *, &, ++, --, +=, -=}
 * @author yukimula
 *
 */
public class SymbolOperator extends SymbolElement {
	
	/** the operator of this node **/
	private COperator operator;
	
	/**
	 * @param operator	{+, -, *, /, %, &, |, ^, <<, >>, &&, ||, <, <=, >, >=, !=, ==, :=, +, -, *, &, ++, --, +=, -=}
	 * @throws Exception
	 */
	private SymbolOperator(COperator operator) throws Exception {
		super(SymbolClass.operator);
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
		case logic_and:
		case logic_or:
		case greater_tn:
		case greater_eq:
		case smaller_tn:
		case smaller_eq:
		case equal_with:
		case not_equals:
		case assign:
		case positive:
		case negative:
		case bit_not:
		case logic_not:
		case address_of:
		case dereference:
		case increment:
		case decrement:
		case arith_add_assign:
		case arith_sub_assign:	
		case bit_xor_assign:	this.operator = operator;	break;
		default:	throw new IllegalArgumentException("unsupport: " + operator);
		}
	}
	
	/**
	 * @return {+, -, *, /, %, &, |, ^, <<, >>, &&, ||, <, <=, >, >=, !=, ==, :=, +, -, *, &, ++, --, +=, -=}
	 */
	public COperator get_operator() { return this.operator; }

	@Override
	protected SymbolNode construct_copy() throws Exception {
		return new SymbolOperator(this.operator);
	}

	@Override
	protected String get_code(boolean simplified) throws Exception {
		switch(operator) {
		case arith_add:			return "+";
		case arith_sub:			return "-";
		case arith_mul:			return "*";
		case arith_div:			return "/";
		case arith_mod:			return "%";
		case bit_and:			return "&";
		case bit_or:			return "|";
		case bit_xor:			return "^";
		case left_shift:		return "<<";
		case righ_shift:		return ">>";
		case logic_and:			return "&&";
		case logic_or:			return "||";
		case greater_tn:		return ">";
		case greater_eq:		return ">=";
		case smaller_tn:		return "<";
		case smaller_eq:		return "<=";
		case equal_with:		return "==";
		case not_equals:		return "!=";
		case assign:			return ":=";
		case positive:			return "+";
		case negative:			return "-";
		case bit_not:			return "~";
		case logic_not:			return "!";
		case address_of:		return "&";
		case dereference:		return "*";
		case increment:			return "++";
		case decrement:			return "--";
		case arith_add_assign:	return "+=";
		case arith_sub_assign:	return "-=";
		case bit_xor_assign:	return "^=";
		default:	throw new IllegalArgumentException("unsupport: " + operator);
		}
	}

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() {
		switch(this.operator) {
		case increment:
		case decrement:
		case arith_add_assign:
		case arith_sub_assign:
		case assign:	return true;
		default:		return false;
		}
	}
	
	/**
	 * @param operator
	 * @return {+, -, *, /, %, &, |, ^, <<, >>, &&, ||, <, <=, >, >=, !=, ==, :=, +, -, *, &, ++, --, +=, -=}
	 * @throws Exception
	 */
	protected static SymbolOperator create(COperator operator) throws Exception {
		return new SymbolOperator(operator);
	}
	
}
