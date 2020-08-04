package com.jcsa.jcparse.lang.cirlang.unit;

import com.jcsa.jcparse.lang.cirlang.expr.CirExpression;

/**
 * <code>
 * 	argument_list |-- ( {expression}* )
 * </code>
 * @author yukimula
 *
 */
public interface CirArgumentList extends CirUnit {
	
	/**
	 * @return the number of arguments in the list
	 */
	public int number_of_arguments();
	
	/**
	 * @param k
	 * @return the kth argument in the list
	 * @throws IndexOutOfBoundsException
	 */
	public CirExpression get_argument(int k) 
					throws IndexOutOfBoundsException;
	
}
