package com.jcsa.jcparse.lang.ir.stmt.assign;

import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.expr.CirReferExpression;
import com.jcsa.jcparse.lang.ir.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

/**
 * reference := value_expression <br>
 * <br>
 * 	|--	|--	<i>assign_statement</i>										<br>
 * 	|--	|--	|-- initial_assign_statement								<br>
 * 	|--	|--	|--	binary_assign_statement									<br>
 * 	|--	|--	|--	increase_assign_statement								<br>
 * 	|--	|--	|--	return_assign_statement									<br>
 * 	|--	|--	|-- wait_assign_statement									<br>
 * 	|--	|--	|--	temporal_assign_statement								<br>
 * 	|--	|--	|--	parameter_assign_statement								<br>
 * 
 * @author yukimula
 *
 */
public interface CirAssignStatement extends CirStatement {
	
	/**
	 * @return =
	 */
	public COperator get_operator();
	
	/**
	 * @return the reference as the left-value to be assigned by the right-value
	 */
	public CirReferExpression get_lvalue();
	
	/**
	 * @return the expression as the right-value to assign left-value
	 */
	public CirExpression get_rvalue();
	
}
