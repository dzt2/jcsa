package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * compute_expression	|--	operator (expression)+
 * @author yukimula
 *
 */
public interface SymComputeExpression extends SymExpression {
	
	/**
	 * get the operator of the expression
	 * @return
	 */
	public COperator get_operator();
	
	/**
	 * get the number of operands in the expression
	 * @return
	 */
	public int number_of_operands();
	
	/**
	 * get the kth operand in the expression
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public SymExpression get_operand(int k) throws IndexOutOfBoundsException;
	
	/**
	 * add the operand in the expression
	 * @param operand
	 * @throws IllegalArgumentException
	 */
	public void add_operand(SymExpression operand) throws IllegalArgumentException;
	
}
