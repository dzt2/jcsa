package com.jcsa.jcparse.parse.symbolic;

import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * 	<code>
 * 		arith:	[neg, add, sub, mul, div, mod]			<br>
 * 		bitws:	[rsv, and, ior, xor, lsh, rsh]			<br>
 * 		logic:	[not, and, ior, eqv, neq, imp(pos)]		<br>
 * 		relate:	[eqv, neq, smt, sme, grt, gre]			<br>
 * 		other:	[adr, ref, ass, inc, dec]				<br>
 * </code>
 * 	
 * 	@author yukimula
 *
 */
public class SymbolOperator extends SymbolElement {
	
	/** the operator that this node represents **/
	private	COperator operator;
	
	/**
	 * It creates a symbolic node to preserve operator used in composite expression
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
	 * @return the operator that this node represents
	 */
	public COperator get_operator() { return this.operator; }
	
	/**
	 * <code>
	 * 		arith:	[neg, add, sub, mul, div, mod]			<br>
	 * 		bitws:	[rsv, and, ior, xor, lsh, rsh]			<br>
	 * 		logic:	[not, and, ior, eqv, neq, imp(pos)]		<br>
	 * 		relate:	[eqv, neq, smt, sme, grt, gre]			<br>
	 * 		other:	[adr, ref, ass, inc, dec]				<br>
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
			/* point_expression */
			case address_of:
			case dereference:
			/* arith_expression */
			case negative:
			case arith_add:
			case arith_sub:
			case arith_mul:
			case arith_div:
			case arith_mod:
			/* bitws_expression */
			case bit_not:
			case bit_and:
			case bit_or:
			case bit_xor:
			case left_shift:
			case righ_shift:
			/* logic_expression + relate_expression */
			case logic_not:
			case logic_and:
			case logic_or:
			case positive:			/* logic_imp */
			case equal_with:		/* logic_eqv, relate_eqv */
			case not_equals:		/* logic_neq, relate_neq */
			case greater_eq:
			case greater_tn:
			case smaller_tn:
			case smaller_eq:
			/* assign_expression */
			case assign:
			case increment:
			case decrement:
			{
				return new SymbolOperator(operator);
			}
			/* unsupported case */
			default:	
			{
				throw new IllegalArgumentException("Unsupported: " + operator);
			}
			}
		}
	}
	
	@Override
	protected SymbolNode new_one() throws Exception { return new SymbolOperator(this.operator); }
	
	@Override
	protected String generate_code(boolean simplified) throws Exception {
		switch(this.operator) {
		/* arith_expression */
		case negative:		return "-";
		case arith_add:		return "+";
		case arith_sub:		return "-";
		case arith_mul:		return "*";
		case arith_div:		return "/";
		case arith_mod:		return "%";
		/* bitws_expression */
		case bit_not:		return "~";
		case bit_and:		return "&";
		case bit_or:		return "|";
		case bit_xor:		return "^";
		case left_shift:	return "<<";
		case righ_shift:	return ">>";
		/* logic_expression */
		case logic_not:		return "!";
		case logic_and:		return "&&";
		case logic_or:		return "||";
		case positive:		return "->";
		case equal_with:	return "==";
		case not_equals:	return "!=";
		/* relate_expression */
		case greater_tn:	return ">";
		case greater_eq:	return ">=";
		case smaller_tn:	return "<";
		case smaller_eq:	return "<=";
		/* other_expression */
		case address_of:	return "&";
		case dereference:	return "*";
		case assign:		return ":=";
		case increment:		return "++";
		case decrement:		return "--";
		default:	throw new IllegalArgumentException("Unsupported: " + this.operator);
		}
	}
	
	@Override
	protected boolean is_refer_type() { return false; }
	
	@Override
	protected boolean is_side_affected() { return false; }
	
}
