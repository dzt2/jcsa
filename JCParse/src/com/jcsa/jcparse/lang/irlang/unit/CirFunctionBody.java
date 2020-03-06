package com.jcsa.jcparse.lang.irlang.unit;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * function_body |--> (statement)+
 * 				 |==> [beg_statement (init_assign_statement)* (statement)* end_statement]
 * 
 * @author yukimula
 *
 */
public interface CirFunctionBody extends CirNode {
	
	/* getters */
	/**
	 * get the number of statements in the function body
	 * @return
	 */
	public int number_of_statements();
	/**
	 * get the kth statement in function body with range from 0 to N - 1
	 * @param k
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public CirStatement get_statement(int k) throws IndexOutOfBoundsException;
	/**
	 * add a new statement at the tail of the function body.
	 * @param statement
	 * @throws IllegalArgumentException
	 */
	public void add_statement(CirStatement statement) throws IllegalArgumentException;
	
	
}
