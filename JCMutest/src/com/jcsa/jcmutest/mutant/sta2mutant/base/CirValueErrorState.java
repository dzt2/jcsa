package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class CirValueErrorState extends CirDataErrorState {

	protected CirValueErrorState(CirExecution execution, CirStateStore store, 
			SymbolExpression ovalue, SymbolExpression mvalue) throws Exception {
		super(execution, store, CirStateValue.set_expr(ovalue, mvalue));
	}
	
	/**
	 * @return the basic value from which the difference is introduced
	 */
	public SymbolExpression get_orig_value() { return this.get_ovalue(); }
	/**
	 * @return the mutation value (or difference) introduced to the target expression
	 */
	public SymbolExpression get_muta_value() { return this.get_mvalue(); }
	
	
}
