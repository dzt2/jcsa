package com.jcsa.jcparse.lang.cirlang.stmt;

import com.jcsa.jcparse.lang.cirlang.expr.CirExpression;
import com.jcsa.jcparse.lang.cirlang.expr.refer.CirReferExpression;

/**
 * <code>
 * 	|--	<i>assign_statement</i>											<br>
 * 	|--	|--	binary_assign_statement										<br>
 * 	|--	|--	initial_assign_statement									<br>
 * 	|--	|--	parameter_assign_statement									<br>
 * 	|--	|--	temporary_assign_statement									<br>
 * 	|--	|--	return_assign_statement										<br>
 * 	|--	|--	wait_assign_statement										<br>
 * 	|--	|--	increase_assign_statement									<br>
 * </code>
 * @author yukimula
 *
 */
public interface CirAssignStatement extends CirStatement {
	
	/**
	 * @return the left-value to be assigned
	 */
	public CirReferExpression get_lvalue();
	
	/**
	 * @return the right-value to assign the left-value
	 */
	public CirExpression get_rvalue();
	
}
