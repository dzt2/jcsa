package com.jcsa.jcparse.lang.ir.expr;

import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * binary_expression |-- expression operator expression
 * @author yukimula
 *
 */
public interface CirBinaryExpression extends CirExpression {
	
	/**
	 * @return the binary operator
	 */
	public COperator get_operator();
	
	/**
	 * @return the left-operand
	 */
	public CirExpression get_loperand();
	
	/**
	 * @return the right-operand
	 */
	public CirExpression get_roperand();
	
}
