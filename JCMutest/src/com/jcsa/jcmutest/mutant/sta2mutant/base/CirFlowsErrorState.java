package com.jcsa.jcmutest.mutant.sta2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * mut_flow(execution, statement, [orig_target, muta_target]);
 * 
 * @author yukimula
 *
 */
public class CirFlowsErrorState extends CirPathErrorState {

	protected CirFlowsErrorState(CirExecution execution, SymbolExpression orig_value,
			SymbolExpression muta_value) throws Exception {
		super(CirStateCategory.mut_flow, execution, orig_value, muta_value);
	}
	
	/**
	 * @return the next statement to be executed in original version
	 */
	public CirExecution get_original_target() { return (CirExecution) this.get_orig_value().get_source(); }
	/**
	 * @return the next statement to be executed in mutated versions
	 */
	public CirExecution get_mutation_target() { return (CirExecution) this.get_muta_value().get_source(); }
	
}
