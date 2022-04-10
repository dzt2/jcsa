package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	set_flow(statement; orig_next, muta_next)
 * 	
 * 	@author yukimula
 *
 */
public class AbsFlowsErrorState extends AbsPathErrorState {

	protected AbsFlowsErrorState(AbsExecutionStore _store, 
			CirExecution orig_next, CirExecution muta_next) throws Exception {
		super(AbsExecutionClass.set_flow, _store, 
				SymbolFactory.sym_expression(orig_next), 
				SymbolFactory.sym_expression(muta_next));
	}
	
	/**
	 * @return the original next statement to be executed after the source
	 */
	public CirExecution get_original_next() { return (CirExecution) this.get_loperand().get_source(); }
	
	/**
	 * @return the mutation next statement to be executed after the source
	 */
	public CirExecution get_mutation_next() { return (CirExecution) this.get_roperand().get_source(); }
	
}
