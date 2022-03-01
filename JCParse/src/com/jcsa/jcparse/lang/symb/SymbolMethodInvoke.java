package com.jcsa.jcparse.lang.symb;

/**
 * It implements the invoke-function of SymbolCallExpression.
 * 
 * @author yukimula
 *
 */
public interface SymbolMethodInvoke {
	
	/**
	 * @param source
	 * @return null if the result is not further evaluated and computed
	 * @throws Exception
	 */
	public SymbolExpression invoke(SymbolCallExpression source) throws Exception;
	
}
