package com.jcsa.jcparse.lang.cirlang.stmt;

import com.jcsa.jcparse.lang.cirlang.expr.CirExpression;
import com.jcsa.jcparse.lang.cirlang.unit.CirArgumentList;

/**
 * call_statement |-- call expression by argument_list
 * @author yukimula
 *
 */
public interface CirCallStatement extends CirStatement {
	
	/**
	 * @return the callee expression to be called
	 */
	public CirExpression get_callee();
	
	/**
	 * @return arguments used for calling function
	 */
	public CirArgumentList get_argument_list();
	
}
