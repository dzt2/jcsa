package com.jcsa.jcparse.lang.ir.stmt.assign;

import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.expr.refer.CirReferExpression;
import com.jcsa.jcparse.lang.ir.stmt.CirStatement;

/**
 * assign_statement |-- reference := expression<br>
 * <br>
 * <code>
 * 	|--	<i>assignment_statement</i>										<br>
 * 	|--	|--	binary_assign_statement										<br>
 * 	|--	|--	initial_assign_statement									<br>
 * 	|--	|--	increase_assign_statement									<br>
 * 	|--	|--	return_assign_statement										<br>
 * 	|--	|--	temporal_assign_statement									<br>
 * 	|--	|--	wait_assign_statement										<br>
 * </code>
 * @author yukimula
 *
 */
public interface CirAssignStatement extends CirStatement {
	
	/**
	 * @return the reference to be assigned
	 */
	public CirReferExpression get_lvalue();
	
	/**
	 * @return the right-value to assign the left-reference
	 */
	public CirExpression get_rvalue();
	
}
