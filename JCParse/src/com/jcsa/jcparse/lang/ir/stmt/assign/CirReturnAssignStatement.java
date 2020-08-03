package com.jcsa.jcparse.lang.ir.stmt.assign;

import com.jcsa.jcparse.lang.ir.expr.refer.CirReturnReferExpression;

/**
 * return_assign_statement |-- return_point := expression
 * 
 * @author yukimula
 *
 */
public interface CirReturnAssignStatement extends CirAssignStatement {
	
	/**
	 * @return the reference that represents the return-point
	 * 			to be assigned in this statement
	 */
	public CirReturnReferExpression get_return_reference();
	
}
