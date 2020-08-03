package com.jcsa.jcparse.lang.ir.unit;

import com.jcsa.jcparse.lang.ir.stmt.CirStatement;

/**
 * statement_list |-- { (init_assign_statement)* (statement)* }
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
	 * @return the kth statement in this list
	 * @throws IndexOutOfBoundsException
	 */
	public CirStatement get_statement(int k) throws IndexOutOfBoundsException;
	
}
