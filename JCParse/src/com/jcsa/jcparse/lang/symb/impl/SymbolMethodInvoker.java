package com.jcsa.jcparse.lang.symb.impl;

import com.jcsa.jcparse.lang.symb.SymbolCallExpression;
import com.jcsa.jcparse.lang.symb.SymbolExpression;

/**
 * 	It implements the invoke or interpretation of SymbolCallExpression.
 * 	
 * 	@author yukimula
 *
 */
public interface SymbolMethodInvoker {
	
	/**
	 * It interprets the call-expression under the given input-output contexts
	 * @param source		the calling-expression as the source for evaluation
	 * @param in_context	the context to provide the input-states to evaluate
	 * @param ou_context	the context to provide the output-state to evaluate
	 * @return				null if the evaluation is not supported in function
	 * @throws Exception
	 */
	public SymbolExpression invoke(SymbolCallExpression source, 
			SymbolContext in_context, SymbolContext ou_context) throws Exception;
	
}
