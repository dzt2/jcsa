package com.jcsa.jcparse.parse.symbolic;

import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * 	<code>
 * 		ArithExpression		{neg, add, sub, mul, div, mod}						<br>
 * 		BitwsExpression		{rsv, and, ior, xor, lsh, rsh}						<br>
 * 		LogicExpression		{not, and, ior, xor, eqv, imp}						<br>
 * 		RelationExpression	{grt, gre, smt, sme, eqv, neq}						<br>
 * 		AssignExpression	{ass, inc, dec}										<br>
 * 	</code>
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
	 * 		ArithExpression		{neg, add, sub, mul, div, mod}				<br>
	 * 		BitwsExpression		{rsv, and, ior, xor, lsh, rsh}				<br>
	 * 		LogicExpression		{not, and, ior, xor, eqv, imp}				<br>
	 * 		RelationExpression	{grt, gre, smt, sme, eqv, neq}				<br>
	 * 		AssignExpression	{ass, inc, dec}								<br>
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
			/* logic_expression, relate_expression */
			case logic_not:
			case logic_and:
			case logic_or:
			case address_of:	/* logic_imp */
			case equal_with:	/* logic_eqv, relate_eqv */
			case not_equals:	/* logic_neq, relate_neq */
			case greater_eq:	
			case greater_tn:
			case smaller_eq:
			case smaller_tn:
			/* assign_expression */
			case assign:
			case increment:
			case decrement:
			{
				return new SymbolOperator(operator);
			}
			/* unsupported case */
			default:	throw new IllegalArgumentException("Invalid: " + operator);
			}
		}
	}

	
	@Override
	protected SymbolNode new_one() throws Exception { return new SymbolOperator(this.operator); }

	
	@Override
	protected String generate_code(boolean simplified) throws Exception {
		switch(this.operator) {
		/* arith_expr */
		case negative:		return "-";
		case arith_add:		return "+";
		case arith_sub:		return "-";
		case arith_mul:		return "*";
		case arith_div:		return "/";
		case arith_mod:		return "%";
		/* bitws_expr */
		case bit_not:		return "~";
		case bit_and:		return "&";
		case bit_or:		return "|";
		case bit_xor:		return "^";
		case left_shift:	return "<<";
		case righ_shift:	return ">>";
		/* logic_expr */
		case logic_not:		return "!";
		case logic_and:		return "&&";
		case logic_or:		return "||";
		case address_of:	return "->";
		/* relate_expr */
		case equal_with:	return "==";
		case not_equals:	return "!=";
		case greater_eq:	return ">=";
		case greater_tn:	return ">";
		case smaller_eq:	return "<=";
		case smaller_tn:	return "<";
		/* assign_expr */
		case assign:		return ":=";
		case increment:		return "++";
		case decrement:		return "--";
		default:	throw new IllegalArgumentException("Unsupported: " + this.operator);
		}
	}
	

	@Override
	protected boolean is_refer_type() { return false; }

	
	@Override
	protected boolean is_side_affected() { 
		switch(this.operator) {
		case assign:
		case increment:
		case decrement:	return true;
		default:		return false;
		}
	}
	
}
