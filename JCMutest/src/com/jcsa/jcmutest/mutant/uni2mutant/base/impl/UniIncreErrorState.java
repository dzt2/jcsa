package com.jcsa.jcmutest.mutant.uni2mutant.base.impl;

import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractClass;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractStore;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	inc_expr(expression; orig_value, difference)
 * 	
 * 	@author yukimula
 *
 */
public class UniIncreErrorState extends UniDataErrorState {

	protected UniIncreErrorState(UniAbstractStore state_store, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(UniAbstractClass.inc_expr, state_store, loperand, roperand);
	}
	
	/**
	 * @return the difference from original value by arith_sub
	 */
	public SymbolExpression get_difference() { return this.get_rvalue(); }
	
}
