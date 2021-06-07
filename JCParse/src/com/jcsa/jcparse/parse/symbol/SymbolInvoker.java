package com.jcsa.jcparse.parse.symbol;

import com.jcsa.jcparse.lang.symbol.SymbolCallExpression;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * It implements the symbolic evaluation on library functions such as printf or sqrt.
 * Each invoker interface accepts an calling-expression with evaluated (simplied) arguments and its names.
 * 
 * @author yukimula
 *
 */
public interface SymbolInvoker {
	
	/**
	 * @param input_expression contians the evaluated (simplified) arguments
	 * @return null if it cannot evaluate it as good results or summaries
	 * @throws Exception
	 */
	public SymbolExpression invoke(SymbolCallExpression input_expression) throws Exception;
	
}
