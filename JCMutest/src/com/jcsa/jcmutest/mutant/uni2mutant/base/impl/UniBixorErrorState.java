package com.jcsa.jcmutest.mutant.uni2mutant.base.impl;

import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractClass;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractStore;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	xor_expr(expression; orig_value, difference)
 * 	
 * 	@author yukimula
 *
 */
public class UniBixorErrorState extends UniDataErrorState {

	protected UniBixorErrorState(UniAbstractStore state_store, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(UniAbstractClass.xor_expr, state_store, loperand, roperand);
	}
	
	/**
	 * @return the difference from original value by bitws_xor
	 */
	public SymbolExpression get_difference() { return this.get_rvalue(); }
	
}
