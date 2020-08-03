package com.jcsa.jcparse.lang.ir.expr.value;

import com.jcsa.jcparse.lang.ir.expr.CirExpression;

/**
 * initializer_list |-- { (expression)* }
 * @author yukimula
 *
 */
public interface CirInitializerList extends CirValueExpression {
	
	/**
	 * @return the number of expressions under the list
	 */
	public int number_of_elements();
	
	/**
	 * @param k
	 * @return the kth element in the initializer list
	 * @throws IndexOutOfBoundsException
	 */
	public CirExpression get_element(int k) throws IndexOutOfBoundsException;
	
}
