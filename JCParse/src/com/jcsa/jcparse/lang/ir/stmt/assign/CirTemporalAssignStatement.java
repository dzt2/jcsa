package com.jcsa.jcparse.lang.ir.stmt.assign;

import com.jcsa.jcparse.lang.ir.expr.refer.CirReturnReferExpression;

/**
 * temporal_expression := expression
 * 
 * @author yukimula
 *
 */
public interface CirTemporalAssignStatement extends CirAssignStatement {
	
	/**
	 * @return the reference that represents the return-point
	 * 			to be assigned in this statement
	 */
	public CirReturnReferExpression get_return_reference();
	
}
