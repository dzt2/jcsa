package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class UniFlowsErrorState extends UniPathErrorState {

	protected UniFlowsErrorState(CirStatement location, 
			CirExecution original_next, 
			CirExecution mutation_next) throws Exception {
		super(UniAbstractClass.set_flow, location, 
				SymbolFactory.sym_expression(original_next),
				SymbolFactory.sym_expression(mutation_next));
	}
	
	/**
	 * @return the next statement being executed next in original version
	 */
	public CirExecution get_original_next() { return (CirExecution) this.get_loperand().get_source(); }
	
	/**
	 * @return the next statement being executed next in mutation version
	 */
	public CirExecution get_mutation_next() { return (CirExecution) this.get_roperand().get_source(); }
	
}
