package com.jcsa.jcparse.lang.symb;

/**
 * It computes the value of call-expression.
 * 
 * @author yukimula
 *
 */
public interface SymInvocate {
	
	/**
	 * @param source
	 * @return the value computed as the result of calling-expression
	 * @throws Exception
	 */
	public SymExpression invocate(SymCallExpression source) throws Exception;
	
}
