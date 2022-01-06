package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class CirIncreErrorState extends CirDataErrorState {

	protected CirIncreErrorState(CirExecution execution, CirStateStore store, 
			SymbolExpression base, SymbolExpression difference) throws Exception {
		super(execution, store, CirStateValue.inc_expr(base, difference));
	}
	
	/**
	 * @return the mutation value (or difference) introduced to the target expression
	 */
	public SymbolExpression get_difference() { return this.get_value().get_rvalue(); }
	
}
