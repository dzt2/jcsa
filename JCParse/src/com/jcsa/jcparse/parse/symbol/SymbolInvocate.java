package com.jcsa.jcparse.parse.symbol;

import com.jcsa.jcparse.lang.symbol.SymbolCallExpression;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public interface SymbolInvocate {

	/**
	 * @param source
	 * @return the value computed as the result of calling-expression
	 * @throws Exception
	 */
	public SymbolExpression invocate(SymbolCallExpression source) throws Exception;
	
}
