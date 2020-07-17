package com.jcsa.jcparse.lang.sym;

/**
 * To interpret the result of invoke-expression (SymFunCallExpression).
 * 
 * @author yukimula
 *
 */
public interface SymInvoke {
	
	/**
	 * @param source the invoke-expression to be interpreted
	 * @return the result interpreted from invoke-expression or null if the method
	 * 			to be invoked is not supported in the invoke machine.
	 * @throws Exception
	 */
	public SymExpression invoke(SymFunCallExpression source) throws Exception;
	
}
