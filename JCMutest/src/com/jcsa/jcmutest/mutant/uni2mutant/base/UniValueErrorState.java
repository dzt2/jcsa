package com.jcsa.jcmutest.mutant.uni2mutant.base;

import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	set_expr(expression; orig_value, muta_value)
 * 	
 * 	@author yukimula
 *
 */
public class UniValueErrorState extends UniDataErrorState {

	protected UniValueErrorState(UniAbstractStore _store, SymbolExpression lvalue,
			SymbolExpression rvalue) throws Exception {
		super(UniAbstractClass.set_expr, _store, lvalue, rvalue);
	}
	
	/**
	 * @return the mutation value to replace the original value
	 */
	public SymbolExpression get_muta_value() { return this.get_roperand(); }

}
