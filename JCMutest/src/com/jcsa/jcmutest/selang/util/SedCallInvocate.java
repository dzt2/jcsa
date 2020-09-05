package com.jcsa.jcmutest.selang.util;

import com.jcsa.jcmutest.selang.lang.expr.SedCallExpression;
import com.jcsa.jcmutest.selang.lang.expr.SedExpression;

/**
 * It is used to interpret the call-expression.
 * 
 * @author yukimula
 *
 */
public interface SedCallInvocate {
	
	/**
	 * @param source
	 * @return the result of the call-expression or null if it is not interpretable.
	 * @throws Exception
	 */
	public SedExpression invocate(SedCallExpression source) throws Exception;
	
}
