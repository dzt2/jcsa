package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * 	The operator used in SymbolCompositeExpression, including:	<br>
 * 	<code>
 * 		unary:	neg, rsv, not, adr, ref							<br>
 * 		arith:	add, sub, mul, div, mod							<br>
 * 		bitws:	and, ior, xor, lsh, rsh							<br>
 * 		logic:	and, ior, neq, eqv, imp(pos)					<br>
 * 		relate:	grt, gre, smt, sme, neq, eqv					<br>
 * 		assign:	ass, inc										<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public class SymbolOperator extends SymbolElement {
	
	/** the operator code of this node **/
	private	COperator operator;
	
	/**
	 * It creates a node representing the operator
	 * @param operator
	 * @throws Exception
	 */
	private SymbolOperator(COperator operator) throws Exception {
		super(SymbolClass.expr_operator);
		if(operator == null) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else {
			this.operator = operator;
		}
	}
	
	/**
	 * @return the operator code of this node
	 */
	public COperator get_operator() { return this.operator; }
	
	/**
	 * <code>
	 * 		unary:	neg, rsv, not, adr, ref							<br>
	 * 		arith:	add, sub, mul, div, mod							<br>
	 * 		bitws:	and, ior, xor, lsh, rsh							<br>
	 * 		logic:	and, ior, neq, eqv, imp(pos)					<br>
	 * 		relate:	grt, gre, smt, sme, neq, eqv					<br>
	 * 		assign:	ass, inc										<br>
	 * </code>
	 * @param operator
	 * @return
	 * @throws Exception
	 */
	protected static SymbolOperator create(COperator operator) throws Exception {
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
			case positive:		/** logics_implication **/
			case not_equals:
			case equal_with:
			case greater_tn:
			case greater_eq:
			case smaller_tn:
			case smaller_eq:
			case assign:		/** explicit assignment **/
			case increment:		/** implicit assignment **/
								return new SymbolOperator(operator);
			default:	throw new IllegalArgumentException(operator.toString());
			}
		}
	}

	@Override
	protected SymbolNode new_one() throws Exception { return new SymbolOperator(this.operator); }

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
		case equal_with:	return "==";
		case not_equals:	return "!=";
		case greater_tn:	return ">";
		case greater_eq:	return ">=";
		case smaller_tn:	return "<";
		case smaller_eq:	return "<=";
		case assign:		return ":=";
		case increment:		return "<-";
		default:		throw new IllegalArgumentException("Unsupport: " + this.operator);
		}
	}

	@Override
	protected boolean is_refer_type() {
		switch(this.operator) {
		case dereference:	return true;
		default:			return false;
		}
	}

	@Override
	protected boolean is_side_affected() {
		switch(this.operator) {
		case assign:
		case increment:		return true;
		default:			return false;
		}
	}
	
}
