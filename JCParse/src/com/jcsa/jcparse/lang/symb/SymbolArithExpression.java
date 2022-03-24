package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * <code> [add, sub, mul, div, mod] </code>
 * @author yukimula
 *
 */
public class SymbolArithExpression extends SymbolBinaryExpression {
	
	/**
	 * It creates an isolated expression for arithmetic
	 * @param data_type
	 * @throws IllegalArgumentException
	 */
	private SymbolArithExpression(CType data_type) throws IllegalArgumentException {
		super(SymbolClass.arith_expression, data_type);
	}

	@Override
	protected SymbolNode new_one() throws Exception {
		return new SymbolArithExpression(this.get_data_type());
	}
	
	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return false; }
	
	/**
	 * @param type		the data type of the expression's value
	 * @param operator	{add, sub, mul, div, mod}
	 * @param loperand	the left-operand
	 * @param roperand	the right-operand
	 * @return			loperand operator roperand
	 * @throws IllegalArgumentException
	 */
	protected static SymbolArithExpression create(CType type, COperator operator,
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
			case arith_add:
			case arith_sub:
			case arith_mul:
			case arith_div:
			case arith_mod:	break;
			default:		throw new IllegalArgumentException("Invalid: " + operator);
			}
			
			SymbolArithExpression expression = new SymbolArithExpression(type);
			expression.add_child(SymbolOperator.create(operator));
			expression.add_child(loperand); expression.add_child(roperand);
			return expression;
		}
	}
	
}
