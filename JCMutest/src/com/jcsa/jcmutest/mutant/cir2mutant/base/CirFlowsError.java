package com.jcsa.jcmutest.mutant.cir2mutant.base;

import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

/**
 * {flw_error; if_exec; orig_target; muta_target;}
 * @author dzt2
 *
 */
public class CirFlowsError extends CirAttribute {

	protected CirFlowsError(CirExecution execution, CirNode location, SymbolExpression parameter)
			throws IllegalArgumentException {
		super(CirAttributeType.flw_error, execution, location, parameter);
	}
	
	/* specialized */
	/**
	 * @return original flow being replaced
	 * @throws Exception
	 */
	public CirExecutionFlow get_orig_flow() throws Exception {
		CirExecution source = this.get_execution();
		CirExecution orig_target = this.get_location().get_tree().
				get_localizer().get_execution((CirStatement) this.get_location());
		for(CirExecutionFlow ou_flow : source.get_ou_flows()) {
			if(ou_flow.get_target() == orig_target) {
				return ou_flow;
			}
		}
		throw new IllegalArgumentException("No flow to original target");
	}
	/**
	 * @return mutation flow to replace with
	 * @throws Exception
	 */
	public CirExecutionFlow get_muta_flow() throws Exception {
		CirExecution source = this.get_execution();
		CirExecution muta_target = (CirExecution) this.get_parameter().get_source();
		return CirExecutionFlow.virtual_flow(source, muta_target);
	}
	
}
