package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * <code>
 * 	SymbolBinaryExpression		{+,-,*,/,%, &,|,^,<<,>>, &&,||, <,<=,>,>=,==,!=, :=}
 * </code>
 * 
 * @author yukimula
 *
 */
public class SymbolBinaryExpression extends SymbolExpression {
	
	private SymbolBinaryExpression(CType type) throws Exception {
		super(SymbolClass.binary_expression, type);
	}
	
	/**
	 * @return	{+,-,*,/,%, &,|,^,<<,>>, &&,||, <,<=,>,>=,==,!=, :=}
	 */
	public SymbolOperator get_operator() { return (SymbolOperator) this.get_child(0); }
	
	/**
	 * @return the left-operand in the binary expression
	 */
	public SymbolExpression get_loperand() { return (SymbolExpression) this.get_child(1); }
	
	/**
	 * @return the right-operand in the binary expression
	 */
	public SymbolExpression get_roperand() { return (SymbolExpression) this.get_child(2); }

	@Override
	protected SymbolNode construct_copy() throws Exception {
		return new SymbolBinaryExpression(this.get_data_type());
	}

	@Override
	protected String get_code(boolean simplified) throws Exception {
		String loperand = this.get_loperand().get_code(simplified);
		if(!this.get_loperand().is_leaf()) {
			loperand = "(" + loperand + ")";
		}
		
		String roperand = this.get_roperand().get_code(simplified);
		if(!this.get_roperand().is_leaf()) {
			roperand = "(" + roperand + ")";
		}
		
		String operator = this.get_operator().get_code(simplified);
		return loperand + " " + operator + " " + roperand;
	}

	@Override
	protected boolean is_refer_type() {
		return false;
	}

	@Override
	protected boolean is_side_affected() {
		switch(this.get_operator().get_operator()) {
		case assign:	return true;
		default:		return false;
		}
	}
	
	/**
	 * @param type
	 * @param operator	{+,-,*,/,%, &,|,^,<<,>>, &&,||, <,<=,>,>=,==,!=, :=}
	 * @param loperand
	 * @param roperand
	 * @return	
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
			case not_equals:
			case equal_with:
			case assign:		break;
			default:	throw new IllegalArgumentException("Invalid operator");
			}
			
			SymbolBinaryExpression expression = new SymbolBinaryExpression(type);
			expression.add_child(SymbolOperator.create(operator));
			expression.add_child(loperand);
			expression.add_child(roperand);
			return expression;
		}
	}
	
}
