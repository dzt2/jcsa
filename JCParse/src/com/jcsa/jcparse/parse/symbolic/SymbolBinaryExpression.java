package com.jcsa.jcparse.parse.symbolic;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * 	<code>
 * 	SymbolBinaryExpression			[add, sub, mul, div, mod, and, ior, xor, lsh, rsh]	<br>
 * 									[and, ior, imp, eqv, neq, grt, gre, smt, sme, ass]	<br>
 * 	</code>
 * 	@author yukimula
 *
 */
public class SymbolBinaryExpression extends SymbolExpression {

	private SymbolBinaryExpression(CType type) throws Exception {
		super(SymbolClass.binary_expression, type);
	}
	
	/**
	 * @return	{add, sub, mul, div, mod, and, ior, xor, lsh, rsh, and, ior, imp(pos), eqv, neq, grt, gre, smt, sme, ass}
	 */
	public SymbolOperator get_operator() { return (SymbolOperator) this.get_child(0); }
	
	/**
	 * @return	{add, sub, mul, div, mod, and, ior, xor, lsh, rsh, and, ior, imp(pos), eqv, neq, grt, gre, smt, sme, ass}
	 */
	public COperator get_coperator() { return this.get_operator().get_operator(); }
	
	/**
	 * @return the left-operand
	 */
	public SymbolExpression get_loperand() { return (SymbolExpression) this.get_child(1); }
	
	/**
	 * @return the right-opernad
	 */
	public SymbolExpression get_roperand() { return (SymbolExpression) this.get_child(2); }

	@Override
	protected SymbolNode new_one() throws Exception { return new SymbolBinaryExpression(this.get_data_type()); }

	@Override
	protected String generate_code(boolean simplified) throws Exception {
		String loperand = this.get_loperand().generate_code(simplified);
		String operator = this.get_operator().generate_code(simplified);
		String roperand = this.get_roperand().generate_code(simplified);
		if(!this.get_loperand().is_leaf()) loperand = "(" + loperand + ")";
		if(!this.get_roperand().is_leaf()) roperand = "(" + roperand + ")";
		return loperand + " " + operator + " " + roperand;
	}

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() {
		switch(this.get_coperator()) {
		case assign:	return true;
		default:		return false;
		}
	}
	
	/**
	 * @param type		the data type of the expression's value
	 * @param operator	{add, sub, mul, div, mod, and, ior, xor, lsh, rsh, and, ior, imp(pos), eqv, neq, grt, gre, smt, sme, ass}
	 * @param loperand	left-operand
	 * @param roperand	right-operand
	 * @return			bin_expr --> loperand operator roperand
	 * @throws Exception
	 */
	protected static SymbolBinaryExpression create(CType type, COperator operator, 
			SymbolExpression loperand, SymbolExpression roperand) throws Exception {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(operator == null) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else if(loperand == null) {
			throw new IllegalArgumentException("Invalid loperand: null");
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			SymbolBinaryExpression expression = new SymbolBinaryExpression(type);
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
			case positive:
			case equal_with:
			case not_equals:
			case greater_tn:
			case greater_eq:
			case smaller_tn:
			case smaller_eq:
			case assign:		expression.add_child(SymbolOperator.create(operator)); break;
			default:			throw new IllegalArgumentException("Unsupport: " + operator);
			}
			if(operator == COperator.assign && !loperand.is_reference()) {
				throw new IllegalArgumentException("Not reference: " + loperand);
			}
			expression.add_child(loperand); expression.add_child(roperand); return expression;
		}
	}

}
