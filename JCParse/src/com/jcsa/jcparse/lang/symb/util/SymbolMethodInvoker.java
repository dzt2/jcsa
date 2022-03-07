package com.jcsa.jcparse.lang.symb.util;

import com.jcsa.jcparse.lang.symb.SymbolCallExpression;
import com.jcsa.jcparse.lang.symb.SymbolExpression;

/**
 * It implements the symbolic invocation on SymbolCallExpression
 * 
 * @author yukimula
 *
 */
public interface SymbolMethodInvoker {
	
	/**
	 * @param source	the call expression to be invoked
	 * @return			the output of the invoked methods or null if undecidable
	 * @throws Exception
	 */
	public SymbolExpression invoke(SymbolCallExpression source) throws Exception;
	
}
