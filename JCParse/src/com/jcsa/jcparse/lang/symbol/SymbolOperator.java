package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * <code>operator -->	[neg, rsv, not, adr, der]
 * 						[add, sub, mul, div, mod]
 * 						[and, ior, xor, lsh, rsh]
 * 						[and, ior, imp(pos)]
 * 						[grt, gre, smt, sme, eqv, neq]
 * 						[ass, ias(inc)]</code>
 * @author yukimula
 *
 */
public class SymbolOperator extends SymbolElement {
	
	/** the operator included **/
	private COperator operator;
	
	/**
	 * It creates an isolated node of operator
	 * @param _class
	 * @throws IllegalArgumentException
	 */
	private SymbolOperator(COperator operator) throws IllegalArgumentException {
		super(SymbolClass.operator);
		if(operator == null) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else {
			switch(operator) {
			case negative:
			case bit_not:
			case logic_not:
			case address_of:
			case dereference:
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
			case positive:		/* as logical implication */
			case greater_tn:
			case greater_eq:
			case smaller_tn:
			case smaller_eq:
			case equal_with:
			case not_equals:
			case assign:
			case increment:		/* as implicit assignment */
								this.operator = operator;	break;
			default:	throw new IllegalArgumentException("Unsupport: " + operator);
			}
		}
	}
	
	/**
	 * @return the operator included in this node
	 */
	public COperator get_operator() { return this.operator; }

	@Override
	protected SymbolNode new_one() throws Exception {
		return new SymbolOperator(this.operator);
	}

	@Override
	protected String generate_code(boolean simplified) throws Exception {
		switch(this.operator) {
		case negative:		return "-";
		case bit_not:		return "~";
		case logic_not:		return "!";
		case address_of:	return "&";
		case dereference:	return "*";
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
		case positive:		return "->";
		case greater_tn:	return ">";
		case greater_eq:	return ">=";
		case smaller_tn:	return "<";
		case smaller_eq:	return "<=";
		case equal_with:	return "==";
		case not_equals:	return "!=";
		case assign:		return ":=";
		case increment:		return "<-";
		default:	throw new IllegalArgumentException(this.operator.toString());
		}
	}

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return false; }
	
	/**
	 * @param operator	{neg, rsv, not, adr, der, add, sub, mul, div, mod, and,
	 * 					ior, xor, lsh, rsh, and, ior, imp(pos), eqv, neq, grt, 
	 * 					gre, smt, sme, ass, ias(inc)}
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected static SymbolOperator create(COperator operator) throws IllegalArgumentException {
		return new SymbolOperator(operator);
	}
	
}
