package com.jcsa.jcparse.lang.ir.stmt.transit;

import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.stmt.CirStatement;
import com.jcsa.jcparse.lang.ir.unit.CirArgumentList;

/**
 * call expression by argument_list.
 * 
 * @author yukimula
 *
 */
public interface CirCallStatement extends CirStatement {
	
	/**
	 * @return the expression of the callee being applied
	 */
	public CirExpression get_callee();
	
	/**
	 * @return the arguments applied to the function calls
	 */
	public CirArgumentList get_arguments();
	
}
