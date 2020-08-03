package com.jcsa.jcparse.lang.ir.expr;

import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * unary_expression	|--	operator operand
 * @author yukimula
 *
 */
public interface CirUnaryExpression extends CirExpression {
	
	/**
	 * @return the operator to be performed
	 */
	public COperator get_operator();
	
	/**
	 * @return unary operand to be used
	 */
	public CirExpression get_operand();
	
}
