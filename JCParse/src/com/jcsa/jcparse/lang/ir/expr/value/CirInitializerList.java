package com.jcsa.jcparse.lang.ir.expr.value;

import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.expr.CirValueExpression;

/**
 * initializer_list	|--	{ expression (, expression)* }
 * @author yukimula
 *
 */
public interface CirInitializerList extends CirValueExpression {
	
	/**
	 * @return the number of elements in the list
	 */
	public int number_of_elements();
	
	/**
	 * @param k
	 * @return the kth element expression in this list
	 * @throws IndexOutOfBoundsException
	 */
	public CirExpression get_element(int k) throws IndexOutOfBoundsException;
	
}
