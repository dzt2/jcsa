package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class CirDiferErrorState extends CirDataErrorState {

	protected CirDiferErrorState(CirExecution point, CirStateStore store, 
			SymbolExpression orig_value, SymbolExpression muta_value) throws Exception {
		super(point, store, CirStateValue.dif_expr(orig_value, muta_value));
	}
	
	/**
	 * @return the original value before the expression is mutated
	 */
	public SymbolExpression get_orig_value() { return this.get_loperand(); }
	
	/**
	 * @return the mutation value after this expression is mutated
	 */
	public SymbolExpression get_muta_value() { return this.get_roperand(); }
	
}
