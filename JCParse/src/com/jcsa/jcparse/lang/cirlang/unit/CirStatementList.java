package com.jcsa.jcparse.lang.cirlang.unit;

import com.jcsa.jcparse.lang.cirlang.stmt.CirStatement;

/**
 * statement_list |-- { 
 * 		(function_beg_statement)
 * 		(param-assign-statement)* 
 * 		(statement)* 
 * 		(function_end_statement)
 * }
 * @author yukimula
 *
 */
public interface CirStatementList extends CirUnit {
	
	/**
	 * @return the number of statements in the list
	 */
	public int number_of_statements();
	
	/**
	 * @param k
	 * @return the kth statement in list
	 * @throws IndexOutOfBoundsException
	 */
	public CirStatement get_statement(int k) 
					throws IndexOutOfBoundsException;
	
}
