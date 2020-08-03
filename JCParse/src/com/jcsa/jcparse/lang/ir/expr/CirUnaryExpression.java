package com.jcsa.jcparse.lang.ir.expr;

import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * unary_expression |-- operator expression
 * @author yukimula
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
