package com.jcsa.jcparse.lang.ir.expr.value;

import com.jcsa.jcparse.lang.ir.expr.CirExpression;

/**
 * return_value_expression |-- wait expression
 * 
 * @author yukimula
 *
 */
public interface CirReturnValueExpression extends CirValueExpression {
	
	/**
	 * @return the expression of function from which the return value is fetched.
	 */
	public CirExpression get_callee();
	
}
