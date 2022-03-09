package com.jcsa.jcparse.parse.symbolic;

/**
 * It implements the evaluation on SymbolCallExpression. 
 * 
 * @author yukimula
 *
 */
public interface SymbolMethodInvoker {
	
	/**
	 * @param source
	 * @return the evaluation result of calling or null for unsupported methods
	 * @throws Exception
	 */
	public SymbolExpression invoke(SymbolCallExpression source) throws Exception;
	
}
