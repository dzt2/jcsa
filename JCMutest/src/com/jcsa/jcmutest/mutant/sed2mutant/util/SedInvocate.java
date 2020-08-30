package com.jcsa.jcmutest.mutant.sed2mutant.util;

import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedCallExpression;
import com.jcsa.jcmutest.mutant.sed2mutant.lang.expr.SedExpression;

/**
 * It implements the computation on SedCallExpression.
 * 
 * @author yukimula
 *
 */
public interface SedInvocate {
	
	/**
	 * @param source
	 * @return the symbolic result of the calling-expression
	 * @throws Exception
	 */
	public SedExpression invocate(SedCallExpression source) throws Exception;
	
}
