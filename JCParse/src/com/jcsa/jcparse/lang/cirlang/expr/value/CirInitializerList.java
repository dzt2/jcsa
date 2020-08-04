package com.jcsa.jcparse.lang.cirlang.expr.value;

import com.jcsa.jcparse.lang.cirlang.expr.CirExpression;

/**
 * <code>
 * 	initializer_list |-- (expression)+
 * </code>
 * @author yukimula
 *
 */
public interface CirInitializerList extends CirValueExpression {
	
	/**
	 * @return the number of expressions in the list
	 */
	public int number_of_elements();
	
	/**
	 * @param k
	 * @return the kth expression in the initializer list
	 * @throws IndexOutOfBoundsException
	 */
	public CirExpression get_element(int k) throws IndexOutOfBoundsException;
	
}
