package com.jcsa.jcmutest.mutant.sad2mutant.eval;

import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadExpression;

/**
 * It performs the evaluation for call-expression.
 * 
 * @author yukimula
 *
 */
public interface SadInvocate {
	
	/**
	 * @param function  the expression of function to be called
	 * @param arguments actual arguments to be applied
	 * @return the result computed from the function w.r.t. the arguments as given or null if the invocation
	 * 		   is not able to be interpreted.
	 * @throws Exception
	 */
	public SadExpression invocate(SadExpression function, Iterable<SadExpression> arguments) throws Exception;
	
}
