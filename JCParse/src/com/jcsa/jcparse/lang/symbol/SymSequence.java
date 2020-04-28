package com.jcsa.jcparse.lang.symbol;

/**
 * sequence |-- { (expression)* }
 * @author yukimula
 *
 */
public interface SymSequence extends SymExpression {
	
	/**
	 * get the number of elements in the sequence
	 * @return
	 */
	public int number_of_elements();
	
	/**
	 * get the kth element in the sequence
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public SymExpression get_element(int k) throws IndexOutOfBoundsException;
	
	/**
	 * add an element in the sequence
	 * @param element
	 * @throws IllegalArgumentException
	 */
	public void add_element(SymExpression element) throws IllegalArgumentException;
	
}
