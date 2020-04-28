package com.jcsa.jcparse.lang.symbol;

/**
 * cast_expression |-- (expression) as {type}
 * @author yukimula
 *
 */
public interface SymCastExpression extends SymExpression {
	
	/**
	 * get the operand to be casted.
	 * @return
	 */
	public SymExpression get_operand();
	
}
