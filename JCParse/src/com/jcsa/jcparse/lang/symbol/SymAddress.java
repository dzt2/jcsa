package com.jcsa.jcparse.lang.symbol;

/**
 * symbolic address is identifier
 * @author yukimula
 *
 */
public interface SymAddress extends SymExpression {
	
	/**
	 * get the symbolic address identifier
	 * @return
	 */
	public String get_address_value();
	
}
