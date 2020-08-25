package com.jcsa.jcmutest.mutant.sad2mutant.eval;

import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadCallExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadExpression;

/**
 * It implements the evaluation on the level of calling function,
 * which returns the symbolic result of the SadCallExpression.
 * 
 * @author yukimula
 *
 */
public interface SadInvocate {
	
	/**
	 * @param call_expression
	 * @return the symbolic result of the function w.r.t. the arguments
	 * 		   or null if the invocation machine cannot interpret it.
	 * @throws Exception
	 */
	public SadExpression invocate(SadCallExpression call_expression) throws Exception;
	
}
