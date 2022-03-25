package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * <code>[grt, gre, smt, sme, eqv, neq]</code>
 * @author yukimula
 *
 */
public class SymbolRelationExpression extends SymbolBinaryExpression {
	
	/**
	 * It creates an isolated expression for arithmetic
	 * @param data_type
	 * @throws IllegalArgumentException
	 */
	private SymbolRelationExpression() throws IllegalArgumentException {
		super(SymbolClass.relation_expression, CBasicTypeImpl.bool_type);
	}

	@Override
	protected SymbolNode new_one() throws Exception {
		return new SymbolRelationExpression();
	}
	
	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return false; }
	
	/**
	 * @param type		the data type of the expression's value
	 * @param operator	{and, ior, imp(pos)}
	 * @param loperand	the left-operand
	 * @param roperand	the right-operand
	 * @return			loperand operator roperand
	 * @throws IllegalArgumentException
	 */
	protected static SymbolRelationExpression create(COperator operator,
			SymbolExpression loperand, SymbolExpression roperand) throws IllegalArgumentException {
		if(operator == null) {
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
			case greater_tn:
			case greater_eq:
			case smaller_tn:
			case smaller_eq:
			case equal_with:
			case not_equals:	break;
			default:		throw new IllegalArgumentException("Invalid: " + operator);
			}
			
			SymbolRelationExpression expression = new SymbolRelationExpression();
			expression.add_child(SymbolOperator.create(operator));
			expression.add_child(loperand); expression.add_child(roperand);
			return expression;
		}
	}
	
}
