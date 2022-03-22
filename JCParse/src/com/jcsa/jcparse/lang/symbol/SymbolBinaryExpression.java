package com.jcsa.jcparse.lang.symbol;

import java.util.Map;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * 	<code>
 * 	|--	|--	SymbolCompositeExpression		(comp_expr --> operator expression)	<br>
 * 	|--	|--	|--	SymbolUnaryExpression		(unary)	[neg, rsv, not, adr, ref]	<br>
 * 	|--	|--	|--	SymbolBinaryExpression		(arith)	[add, sub, mul, div, mod]	<br>
 * 	|--	|--	|--	SymbolBinaryExpression		(bitws)	[and, ior, xor, lsh, rsh]	<br>
 * 	|--	|--	|--	SymbolBinaryExpression		(logic)	[and, ior, eqv, neq, imp]	<br>
 * 	|--	|--	|--	SymbolBinaryExpression		(relate)[grt, gre, smt, sme, neq...]<br>
 * 	|--	|--	|--	SymbolBinaryExpression		(assign)[ass, pss]					<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public class SymbolBinaryExpression extends SymbolCompositeExpression {

	private SymbolBinaryExpression(CType type) throws Exception {
		super(SymbolClass.binary_expression, type);
	}
	
	/**
	 * @return left-operand
	 */
	public SymbolExpression get_loperand() { return this.get_operand(0); } 
	
	/**
	 * @return right-operand
	 */
	public SymbolExpression get_roperand() { return this.get_operand(1); }

	@Override
	protected SymbolNode new_one() throws Exception { 
		return new SymbolBinaryExpression(this.get_data_type()); 
	}

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
		case assign:
		case increment:	return true;
		default:		return false;
		}
	}
	
	/**
	 * @param type		the data type of binary expression value
	 * @param operator	[add, sub, mul, div, mod, and, ior, xor, lsh, rsh,
	 * 					and, ior, imp, eqv, neq, grt, gre, smt, sme, ass, inc]
	 * @param loperand	left-operand
	 * @param roperand	right-operand
	 * @return			(type) (loperand operator roperand)
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
			SymbolBinaryExpression expression;
			expression = new SymbolBinaryExpression(type);
			switch(operator) {
			case assign:
			case increment:
			{
				if(!loperand.is_reference()) {
					throw new IllegalArgumentException("Not a reference: " + loperand);
				}
				break;
			}
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
			{
				break;
			}
			default:	throw new IllegalArgumentException(operator.toString());
			}
			expression.add_child(SymbolOperator.create(operator));
			expression.add_child(loperand);
			expression.add_child(roperand);
			return expression;
		}
	}

	@Override
	protected SymbolExpression symb_replace(Map<SymbolExpression, SymbolExpression> name_value_map) throws Exception {
		CType data_type = this.get_data_type();
		COperator operator = this.get_coperator();
		SymbolExpression loperand = this.get_loperand();
		SymbolExpression roperand = this.get_roperand();
		loperand = loperand.symb_replace(name_value_map);
		roperand = roperand.symb_replace(name_value_map);
		return create(data_type, operator, loperand, roperand);
	}
	
}
