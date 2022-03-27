package com.jcsa.jcmutest.mutant.uni2mutant.base;

import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * 	<code>
 * 	UniAbstractState				[st_class, c_loct; lsh_value, rsh_value]	<br>
 * 	|--	UniDataErrorState			[st_class, c_expr; orig_expr, parameter]	<br>
 * 	|--	|--	UniValueErrorState		[set_expr, c_expr; orig_expr, muta_expr]	<br>
 * 	|--	|--	UniIncreErrorState		[inc_expr, c_expr; orig_expr, different]	<br>
 * 	|--	|--	UniBixorErrorState		[xor_expr, c_expr; orig_expr, different]	<br>
 * 	</code>
 * 	
 * 	@author yukimula
 *
 */
public abstract class UniDataErrorState extends UniAbstractState {
	
	/**
	 * @param _class
	 * @param _store
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	protected UniDataErrorState(UniAbstractClass _class, UniAbstractStore _store, 
			SymbolExpression lvalue, SymbolExpression rvalue) throws Exception {
		super(_class, _store, lvalue, rvalue);
		if(!_store.is_expr()) {
			throw new IllegalArgumentException("Expression-Store required.");
		}
	}
	
	/**
	 * @return the original expression where the data error arises
	 */
	public CirExpression get_expression() { return (CirExpression) this.get_state_store().get_cir_location(); }
	
	/**
	 * @return the original value hold by this expression point
	 */
	public SymbolExpression get_orig_value() { return this.get_loperand(); }
	
}
