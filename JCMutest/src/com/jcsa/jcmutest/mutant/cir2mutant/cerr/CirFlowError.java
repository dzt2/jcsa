package com.jcsa.jcmutest.mutant.cir2mutant.cerr;

import com.jcsa.jcmutest.mutant.cir2mutant.CirErrorType;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.test.state.CStateContexts;


/**
 * <code>set_flow(statement, orig_flow, muta_flow)</code>: the original execution
 * flow starting from the statement is mutated to another point from the original
 * to another point through the mutation flow.<br>
 * 
 * @author yukimula
 *
 */
public class CirFlowError extends CirStateError {
	
	/** original flow being replaced **/
	private CirExecutionFlow orig_flow;
	/** mutation flow which replaces the original **/
	private CirExecutionFlow muta_flow;
	/**
	 * @param orig_flow original flow being replaced
	 * @param muta_flow mutation flow which replaces the original
	 * @throws Exception
	 */
	protected CirFlowError(CirExecutionFlow orig_flow, 
			CirExecutionFlow muta_flow) throws Exception {
		super(CirErrorType.flow_error, orig_flow.get_source().get_statement());
		if(muta_flow == null)
			throw new IllegalArgumentException("Invalid muta_flow: null");
		else if(orig_flow.get_source() != muta_flow.get_source())
			throw new IllegalArgumentException("Unable to match source");
		else {
			this.orig_flow = orig_flow;
			this.muta_flow = muta_flow;
		}
	}
	
	/* getters */
	/**
	 * @return original flow being replaced
	 */
	public CirExecutionFlow get_original_flow() { return this.orig_flow; }
	/**
	 * @return mutation flow which replaces the original
	 */
	public CirExecutionFlow get_mutation_flow() { return this.muta_flow; }

	@Override
	protected String generate_code() throws Exception {
		return this.orig_flow.get_target() + ", " + this.muta_flow.get_target();
	}
	
	@Override
	public Boolean validate(CStateContexts contexts) throws Exception {
		return this.orig_flow.get_target() != this.muta_flow.get_target();
	}
	
}
