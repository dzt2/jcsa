package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;

/**
 * statement |-- (expression)*
 * @author yukimula
 *
 */
public interface SymStatement extends SymNode {
	
	/**
	 * get the execution node to which the statement refers
	 * @return
	 */
	public CirExecution get_execution();
	
	/**
	 * set the execution node to which the statement refers
	 * @param execution
	 * @throws IllegalArgumentException
	 */
	public void set_execution(CirExecution execution) throws IllegalArgumentException;
	
	/**
	 * get the number of the expressions in the statement
	 * @return
	 */
	public int number_of_expressions();
	
	/**
	 * get the kth expression in the statement
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public SymExpression get_expression(int k) throws IndexOutOfBoundsException;
	
	/**
	 * add a new expression under the statement
	 * @param expression
	 * @throws IllegalArgumentException
	 */
	public void add_expression(SymExpression expression) throws IllegalArgumentException;
	
}
