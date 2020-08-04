package com.jcsa.jcparse.lang.cirlang.expr.value;

import com.jcsa.jcparse.lang.cirlang.expr.CirExpression;

/**
 * <code>
 * 	return_value_expression |-- return_of(function)
 * </code>
 * @author yukimula
 *
 */
public interface CirReturnValueExpression extends CirValueExpression {
	
	/**
	 * @return the callee from which the return value is generated
	 */
	public CirExpression get_callee();
	
}
