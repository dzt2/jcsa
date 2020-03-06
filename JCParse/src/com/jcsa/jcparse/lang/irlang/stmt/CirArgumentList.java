package com.jcsa.jcparse.lang.irlang.stmt;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;

/**
 * argument_list --> (expression)*
 * @author yukimula
 *
 */
public interface CirArgumentList extends CirNode {
	public int number_of_arguments();
	public CirExpression get_argument(int k) throws IndexOutOfBoundsException;
	public void add_argument(CirExpression argument) throws IllegalArgumentException;
}
