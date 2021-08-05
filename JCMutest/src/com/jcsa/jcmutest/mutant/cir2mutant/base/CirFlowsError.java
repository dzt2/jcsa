package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;

public class CirFlowsError extends CirAttribute {

	protected CirFlowsError(CirExecutionFlow orig_flow, SymbolExpression parameter)
			throws IllegalArgumentException {
		super(CirAttributeType.flw_error, orig_flow.get_source(),
				orig_flow.get_target().get_statement(), parameter);
	}

	/**
	 * @return the original execution flow being replaced with
	 * @throws Exception
	 */
	public CirExecutionFlow get_original_flow() throws Exception {
		CirExecution source = this.get_execution();
		CirStatement target_statement = (CirStatement) this.get_location();
		CirExecution target = target_statement.
				get_tree().get_localizer().get_execution(target_statement);
		for(CirExecutionFlow flow : source.get_ou_flows()) {
			if(flow.get_target() == target) return flow;
		}
		throw new IllegalArgumentException("Invalid source: " + source);
	}
	/**
	 * @return the flow to replace the original flow in the error
	 * @throws Exception
	 */
	public CirExecutionFlow get_mutation_flow() throws Exception {
		CirExecution source = this.get_execution();
		CirExecution target = (CirExecution) this.get_parameter().get_source();
		return CirExecutionFlow.virtual_flow(source, target);
	}

	@Override
	public CirAttribute optimize(SymbolProcess context) throws Exception {
		return this;
	}

	@Override
	public Boolean evaluate(SymbolProcess context) throws Exception {
		return this.get_original_flow().get_target() != this.get_mutation_flow().get_target();
	}

}
