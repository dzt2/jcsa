package com.jcsa.jcparse.lang.sym;

import com.jcsa.jcparse.lang.lexical.COperator;

/**	
 * 	operator |-- {operator: COperator}
 * 	@author yukimula
 *	
 */
public class SymOperator extends SymUnit {
	
	/* definition */
	private COperator operator;
	private SymOperator(COperator operator) throws IllegalArgumentException {
		if(operator == null)
			throw new IllegalArgumentException("Invalid operator: null");
		else
			this.operator = operator;
	}
	
	/**
	 * @return the operator of the node
	 */
	public COperator get_operator() { return this.operator; }
	
	@Override
	protected SymNode construct() throws Exception {
		return new SymOperator(this.operator);
	}
	
	/**
	 * @param operator
	 * @return symbolic node to represent the operator
	 * @throws Exception
	 */
	protected static SymOperator create(COperator operator) throws Exception {
		return new SymOperator(operator);
	}
	
}
