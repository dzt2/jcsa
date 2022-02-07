package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

/**
 * mut_flow([statement, stmt_key]; [orig_target, muta_target])
 * 
 * @author yukimula
 *
 */
public class CirFlowsErrorState extends CirPathErrorState {

	protected CirFlowsErrorState(CirExecutionFlow orig_flow, 
			CirExecutionFlow muta_flow) throws Exception {
		super(CirAbstractClass.mut_flow, orig_flow.get_source(), 
				SymbolFactory.sym_expression(orig_flow.get_target()), 
				SymbolFactory.sym_expression(muta_flow.get_target()));
	}
	
	/* special getters */
	/**
	 * @return the original source from which the flow is mutated
	 */
	public CirExecution get_source() { return this.get_execution(); }
	/**
	 * @return the statement being executed next to source in the original version
	 */
	public CirExecution get_orig_target() { 
		return (CirExecution) this.get_loperand().get_source(); 
	}
	/**
	 * @return the statement being executed next to source in the mutated version
	 */
	public CirExecution get_muta_target() { 
		return (CirExecution) this.get_roperand().get_source(); 
	}
	
}
