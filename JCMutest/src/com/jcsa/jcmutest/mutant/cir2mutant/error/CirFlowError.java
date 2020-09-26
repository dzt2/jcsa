package com.jcsa.jcmutest.mutant.cir2mutant.error;

import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

/**
 * flow_error(statement, original_flow, mutation_flow) requires the original flow
 * from the statement being mutated to another with mutation flow.
 * 
 * @author yukimula
 *
 */
public class CirFlowError extends CirStateError {
	
	/** original flow from the statement being replaced **/
	private CirExecutionFlow original_flow;
	/** mutation flow which replaces the original flow **/
	private CirExecutionFlow mutation_flow;
	
	protected CirFlowError(CirStatement statement,
			CirExecutionFlow original_flow,
			CirExecutionFlow mutation_flow) throws Exception {
		super(CirErrorType.flow_error, statement);
		if(original_flow == null 
			|| original_flow.get_source().get_statement() != this.get_statement())
			throw new IllegalArgumentException("Invalid original_flow");
		else if(mutation_flow == null
				|| mutation_flow.get_source().get_statement() != this.get_statement())
			throw new IllegalArgumentException("Invalid mutation_flow");
		else {
			this.original_flow = original_flow;
			this.mutation_flow = mutation_flow;
		}
	}
	
	/* getters */
	/**
	 * @return original flow from the statement being replaced
	 */
	public CirExecutionFlow get_original_flow() { return this.original_flow; }
	/**
	 * @return mutation flow which replaces the original flow
	 */
	public CirExecutionFlow get_mutation_flow() { return this.mutation_flow; }
	@Override
	protected String generate_code() throws Exception {
		return this.original_flow.get_target().toString() + 
				", " + this.mutation_flow.get_target().toString();
	}
	
}
