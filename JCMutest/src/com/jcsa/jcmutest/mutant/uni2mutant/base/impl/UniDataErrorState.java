package com.jcsa.jcmutest.mutant.uni2mutant.base.impl;

import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractClass;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractState;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractStore;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	It represents a data state error injected at some CirExpression location.
 * 	<br>
 * 	<code>
 * 	UniDataErrorState			class(expression; orig_value, parameter)		<br>
 * 	|--	UniValueErrorState		set_expr(expression; orig_value, muta_value)	<br>
 * 	|--	UniIncreErrorState		inc_expr(expression; orig_value, difference)	<br>
 * 	|--	UniBixorErrorState		xor_expr(expression; orig_value, difference)	<br>
 * 	</code>
 * 	@author yukimula
 *
 */
public abstract class UniDataErrorState extends UniAbstractState {

	protected UniDataErrorState(UniAbstractClass state_class, UniAbstractStore state_store, SymbolExpression loperand,
			SymbolExpression roperand) throws Exception {
		super(state_class, state_store, loperand, roperand);
		if(state_store.is_expression()) { /* valid case */ }
		else {
			throw new IllegalArgumentException("Invalid: " + state_store);
		}
	}
	
	/**
	 * @return the expression location where this data state error is introduced
	 */
	public CirExpression get_expression() { return (CirExpression) this.get_store().get_cir_location(); }
	
	/**
	 * @return the original value hold by the expression in original version
	 */
	public SymbolExpression get_orig_value() { return this.get_lvalue(); }
	
}
