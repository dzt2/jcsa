package com.jcsa.jcparse.lang.irlang.expr;

import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * compute-expression is defined as (operator operand+)
 * @author yukimula
 *
 */
public interface CirComputeExpression extends CirValueExpression {
	public COperator get_operator();
	public int number_of_operand();
	public CirExpression get_operand(int k) throws IndexOutOfBoundsException;
	public void add_operand(CirExpression operand) throws IllegalArgumentException;
}
