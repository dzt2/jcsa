package com.jcsa.jcparse.lang.symbol;

/**
 * defer_expression |-- *(expression)
 * @author yukimula
 *
 */
public interface SymDeferExpression extends SymExpression {
	
	/**
	 * get the operand to be refered
	 * @return
	 */
	public SymExpression get_operand();
	
}
