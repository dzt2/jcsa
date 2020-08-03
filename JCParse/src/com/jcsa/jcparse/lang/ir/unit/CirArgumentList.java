package com.jcsa.jcparse.lang.ir.unit;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.expr.CirExpression;

/**
 * argument_list |-- ( expression+ )
 * @author yukimula
 *
 */
public interface CirArgumentList extends CirNode {
	
	/**
	 * @return the number of arguments in the list
	 */
	public int number_of_arguments();
	
	/**
	 * @param k
	 * @return the kth argument in the list
	 * @throws IndexOutOfBoundsException
	 */
	public CirExpression get_argument(int k) throws IndexOutOfBoundsException;
	
}
