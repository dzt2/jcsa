package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.lexical.CConstant;

/**
 * 
 * @author yukimula
 *
 */
public interface SymConstant extends SymExpression {
	
	/**
	 * get the constant that the node represents
	 * @return
	 */
	public CConstant get_constant();
	
}
