package com.jcsa.jcmutest.mutant.cir2mutant.cerr;

import com.jcsa.jcparse.flwa.symbol.CStateContexts;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;

public class SymFlowError extends SymStateError {
	
	/** the original flow from the source statement being mutated **/
	private CirExecutionFlow orig_flow;
	/** the mutation flow that replace the original flow from testing **/
	private CirExecutionFlow muta_flow;
	
	/**
	 * @param execution
	 * @param orig_flow
	 * @param muta_flow
	 * @throws IllegalArgumentException
	 */
	protected SymFlowError(CirExecution execution, CirExecutionFlow orig_flow, CirExecutionFlow muta_flow) throws IllegalArgumentException {
		super(SymInstanceType.flow_error, execution, execution.get_statement());
		if(orig_flow == null || orig_flow.get_source() != execution)
			throw new IllegalArgumentException("Invalid orig_flow: " + orig_flow);
		else if(muta_flow == null || muta_flow.get_source() != execution)
			throw new IllegalArgumentException("Invalid muta_flow: " + muta_flow);
		else {
			this.orig_flow = orig_flow;
			this.muta_flow = muta_flow;
		}
	}
	
	/**
	 * @return the original flow from the source statement being mutated
	 */
	public CirExecutionFlow get_original_flow() { return this.orig_flow; }
	/**
	 * @return the mutation flow that replace the original flow from testing
	 */
	public CirExecutionFlow get_mutation_flow() { return this.muta_flow; }

	@Override
	protected String generate_code() throws Exception {
		return this.get_type() + 
				":" + this.get_execution() + "(" + 
				this.orig_flow.get_target() + ", " + 
				this.muta_flow.get_target() + ")";
	}
	@Override
	public Boolean validate(CStateContexts contexts) throws Exception {
		return this.orig_flow.get_target() != this.muta_flow.get_target();
	}
	
}
