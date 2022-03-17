package com.jcsa.jcparse.lang.symbol;

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
	public SymbolExpression invoke(SymbolCallExpression source, SymbolProcess ou_state) throws Exception;
	
}
