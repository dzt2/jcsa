package com.jcsa.jcmutest.mutant.uni2mutant.base.impl;

import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractClass;
import com.jcsa.jcmutest.mutant.uni2mutant.base.UniAbstractStore;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * 	mut_flow(source; orig_next, muta_next)
 * 	
 * 	@author yukimula
 *
 */
public class UniFlowsErrorState extends UniPathErrorState {

	protected UniFlowsErrorState(UniAbstractStore state_store, CirExecution orig_next, CirExecution muta_next) throws Exception {
		super(UniAbstractClass.mut_flow, state_store, 
				SymbolFactory.sym_expression(orig_next), 
				SymbolFactory.sym_expression(muta_next));
	}
	
	/**
	 * @return the next statement being executed from source in original version
	 */
	public CirExecution get_orig_next() { return (CirExecution) this.get_lvalue().get_source(); }
	
	/**
	 * @return the next statement being executed from source in mutation version
	 */
	public CirExecution get_muta_next() { return (CirExecution) this.get_rvalue().get_source(); }
	
}
