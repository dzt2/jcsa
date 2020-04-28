package com.jcsa.jcparse.lang.symbol;

/**
 * field_expression |-- expression.field
 * @author yukimula
 *
 */
public interface SymFieldExpression extends SymExpression {
	
	/**
	 * get the body expression
	 * @return
	 */
	public SymExpression get_body();
	
	/**
	 * get the field as bias
	 * @return
	 */
	public SymField get_field();
	
}
