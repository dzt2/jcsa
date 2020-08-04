package com.jcsa.jcparse.lang.cirlang.expr;

import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * 	unary_expression |-- {-, ~, !, &, *} expression
 * 	
 * 	@author yukimula
 *	
 */
public interface CirUnaryExpression extends CirExpression {
	
	/**
	 * @return the unary operator
	 */
	public COperator get_operator();
	
	/**
	 * @return the unary operand
	 */
	public CirExpression get_operand();
	
}
