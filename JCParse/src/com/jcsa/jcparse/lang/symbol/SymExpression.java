package com.jcsa.jcparse.lang.symbol;

import com.jcsa.jcparse.lang.ctype.CType;

/**
 * 	Expression
 * 	|--	Address
 * 	|--	Constant
 * 	|--	Literal
 * 	|--	Sequence
 * 	|--	CompositeExpression
 * 	|--	CastExpression
 * 	|--	DeferExpression
 * 	|--	FieldExpression
 * 	|--	CallExpression
 * @author yukimula
 *
 */
public interface SymExpression extends SymNode {
	
	/**
	 * get the data type of the expression
	 * @return
	 */
	public CType get_data_type();
	
	/**
	 * get the statement to which the expression belongs
	 * @return
	 */
	public SymStatement get_statement();
	
}
