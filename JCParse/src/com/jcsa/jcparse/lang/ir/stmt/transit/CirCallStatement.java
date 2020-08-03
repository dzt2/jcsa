package com.jcsa.jcparse.lang.ir.stmt.transit;

import com.jcsa.jcparse.lang.ir.expr.CirExpression;
import com.jcsa.jcparse.lang.ir.stmt.CirStatement;
import com.jcsa.jcparse.lang.ir.unit.CirArgumentList;

/**
 * call expression by argument_list
 * @author yukimula
 *
 */
public interface CirCallStatement extends CirStatement {
	
	/**
	 * @return the expression of the function to be called
	 */
	public CirExpression get_callee();
	
	/**
	 * @return the arguments to be used for calling function
	 */
	public CirArgumentList get_arguments();
	
}
