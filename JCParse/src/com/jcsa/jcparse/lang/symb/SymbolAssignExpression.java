package com.jcsa.jcparse.lang.symb;

import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.lexical.COperator;

public class SymbolAssignExpression extends SymbolBinaryExpression {
	
	/**
	 * It creates an isolated node to assign expression
	 * @param data_type
	 * @throws IllegalArgumentException
	 */
	private SymbolAssignExpression(CType data_type) throws IllegalArgumentException {
		super(SymbolClass.assign_expression, data_type);
	}

	@Override
	protected SymbolNode new_one() throws Exception {
		return new SymbolAssignExpression(this.get_data_type());
	}

	@Override
	protected boolean is_refer_type() { return false; }

	@Override
	protected boolean is_side_affected() { return true; }
	
	/**
	 * @param type
	 * @param operator
	 * @param loperand
	 * @param roperand
	 * @return	it creates an assign-expression
	 * @throws IllegalArgumentException
	 */
	protected static SymbolAssignExpression create(CType type, COperator operator,
			SymbolExpression loperand, SymbolExpression roperand) throws IllegalArgumentException {
		if(type == null) {
			throw new IllegalArgumentException("Invalid type: null");
		}
		else if(operator == null) {
			throw new IllegalArgumentException("Invalid operator: null");
		}
		else if(loperand == null || !loperand.is_refer_type()) {
			throw new IllegalArgumentException("Invalid loperand: " + loperand);
		}
		else if(roperand == null) {
			throw new IllegalArgumentException("Invalid roperand: null");
		}
		else {
			switch(operator) {
			case assign:
			case increment:	break;
			default:	throw new IllegalArgumentException("Invalid: " + operator);
			}
			
			SymbolAssignExpression expression = new SymbolAssignExpression(type);
			expression.add_child(SymbolOperator.create(operator));
			expression.add_child(loperand); expression.add_child(roperand);
			return expression;
		}
	}
	
}
