package com.jcsa.jcparse.lang.ir.unit;

import com.jcsa.jcparse.lang.ir.CirNode;
import com.jcsa.jcparse.lang.ir.stmt.CirStatement;

/**
 * statement_list	|--	beg_statement {parameter_assign_statement}* {statement}+ end_statemen
 * @author yukimula
 *
 */
public interface CirStatementList extends CirNode {
	
	/**
	 * @return the number of statements in the list
	 */
	public int number_of_statements();
	
	/**
	 * @return the kth statement in the list
	 */
	public CirStatement get_statement(int k) throws IndexOutOfBoundsException;
	
}
