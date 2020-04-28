package com.jcsa.jcparse.lang.symbol;

/**
 * ArgumentList	|--	{expression}*
 * @author yukimula
 *
 */
public interface SymArgumentList extends SymNode {
	
	/**
	 * get the number of arguments in the list
	 * @return
	 */
	public int number_of_arguments();
	
	/**
	 * get the kth argument in the list
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public SymExpression get_argument(int k) throws IndexOutOfBoundsException;
	
	/**
	 * add a new argument in the list
	 * @param argument
	 * @return
	 * @throws IllegalArgumentException
	 */
	public void add_argument(SymExpression argument) throws IllegalArgumentException;
	
}
