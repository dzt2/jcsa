package com.jcsa.jcmutest.backups;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class TTRPMutationParser extends CirMutationParser {

	@Override
	protected void parse(CirTree tree, AstMutation source, List<CirMutation> targets) throws Exception {
		int loop_time = ((Integer) source.get_parameter()).intValue();
		CirStatement if_statement = (CirStatement) get_cir_nodes(tree, 
					source.get_location(), CirIfStatement.class).get(0);
		CirExecution if_execution = this.get_execution(if_statement);
		for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
			if(flow.get_type() == CirExecutionFlowType.true_flow) {
				CirExecution next_execution = flow.get_target();
				targets.add(CirMutations.trap_on_stmt(next_execution.get_statement(), loop_time));
			}
		}
	}

}
