package com.jcsa.jcmutest.mutant.uni2mutant.base.impl;

import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractClass;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractStore;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	set_expr(expression; orig_value, difference)
 * 	
 * 	@author yukimula
 *
 */
public class UniValueErrorState extends UniDataErrorState {

	protected UniValueErrorState(UniAbstractStore state_store, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(UniAbstractClass.set_expr, state_store, loperand, roperand);
	}
	
	/**
	 * @return the value to replace the original expression
	 */
	public SymbolExpression get_muta_value() { return this.get_rvalue(); }
	
}
