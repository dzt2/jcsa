package com.jcsa.jcmutest.mutant.uni2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	mut_flow(source; orig_next, muta_next)
 * 	
 * 	@author yukimula
 *
 */
public class UniFlowsErrorState extends UniPathErrorState {
	
	/**
	 * @param _store	the source statement from which that flow is mutated
	 * @param orig_next	the statement being executed next in original version
	 * @param muta_next	the statement being executed next in mutation version
	 * @throws Exception
	 */
	protected UniFlowsErrorState(UniAbstractStore _store, CirExecution 
			orig_next, CirExecution muta_next) throws Exception {
		super(UniAbstractClass.mut_flow, _store, 
				SymbolFactory.sym_expression(orig_next), 
				SymbolFactory.sym_expression(muta_next));
	}
	
	/**
	 * @return	the statement being executed next in original version
	 */
	public CirExecution get_original_next() { return (CirExecution) this.get_loperand().get_source(); }
	
	/**
	 * @return	the statement being executed next in mutation version
	 */
	public CirExecution get_mutation_next() { return (CirExecution) this.get_roperand().get_source(); }
	
}
