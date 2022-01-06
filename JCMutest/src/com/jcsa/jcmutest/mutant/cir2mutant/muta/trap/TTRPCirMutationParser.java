package com.jcsa.jcmutest.mutant.cir2mutant.muta.trap;

import java.util.Map;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class TTRPCirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return (CirStatement) this.get_cir_node(
				cir_tree, mutation.get_location(), CirIfStatement.class);
	}

	@Override
	protected void generate_infections(CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<CirAttribute, CirAttribute> infections) throws Exception {
		/* obtain the condition attributes */
		CirStatement if_statement = (CirStatement) this.get_cir_node(
				cir_tree, mutation.get_location(), CirIfStatement.class);
		CirExecution if_execution = cir_tree.get_localizer().get_execution(if_statement);
		CirExecution true_branch = null;
		for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
			if(flow.get_type() == CirExecutionFlowType.true_flow) {
				true_branch = flow.get_target();
				break;
			}
		}
		int times = ((Integer) mutation.get_parameter()).intValue();

		/* construct cir-based mutation here */
		CirAttribute constraint = CirAttribute.new_cover_count(true_branch, times);
		CirAttribute init_error = CirAttribute.new_traps_error(true_branch);
		infections.put(init_error, constraint);
	}

}
