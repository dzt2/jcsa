package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class CirFlowsErrorState extends CirPathErrorState {
	
	protected CirFlowsErrorState(CirAbstractStore location, 
			CirExecution orig_next, CirExecution muta_next) throws Exception {
		super(CirAbstractClass.mut_flow, location, 
				SymbolFactory.sym_expression(orig_next),
				SymbolFactory.sym_expression(muta_next));
	}
	
	/**
	 * @return the statement being next to this state in original program
	 */
	public CirExecution get_original_next() { return (CirExecution) this.get_loperand().get_source(); }
	
	/**
	 * @return the statement being next to this state in mutation program
	 */
	public CirExecution get_mutation_next() { return (CirExecution) this.get_roperand().get_source(); }
	
}
