package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * <code> [and, ior, xor, lsh, rsh] </code>
 * @author yukimula
 *
 */
public class SymbolBitwsExpression extends SymbolBinaryExpression {
	
	/**
	 * It creates an isolated expression for arithmetic
	 * @param data_type
	 * @throws IllegalArgumentException
	 */
	private SymbolBitwsExpression(CType data_type) throws IllegalArgumentException {
		super(SymbolClass.bitws_expression, data_type);
	}

	@Override
	protected SymbolNode new_one() throws Exception {
		return new SymbolBitwsExpression(this.get_data_type());
	}
	
	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return false; }
	
	/**
	 * @param type		the data type of the expression's value
	 * @param operator	{and, ior, xor, lsh, rsh}
	 * @param loperand	the left-operand
	 * @param roperand	the right-operand
	 * @return			loperand operator roperand
	 * @throws IllegalArgumentException
	 */
	protected static SymbolBitwsExpression create(CType type, COperator operator,
			SymbolExpression loperand, SymbolExpression roperand) throws IllegalArgumentException {
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
			case bit_and:
			case bit_or:
			case bit_xor:
			case left_shift:
			case righ_shift:	break;
			default:			throw new IllegalArgumentException("Invalid: " + operator);
			}
			
			SymbolBitwsExpression expression = new SymbolBitwsExpression(type);
			expression.add_child(SymbolOperator.create(operator));
			expression.add_child(loperand); expression.add_child(roperand);
			return expression;
		}
	}
	
}
