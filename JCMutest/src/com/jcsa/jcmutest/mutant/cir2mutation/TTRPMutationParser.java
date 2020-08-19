package com.jcsa.jcmutest.mutant.cir2mutation;

import java.util.Collection;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutation;
import com.jcsa.jcmutest.mutant.mutation.CirMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class TTRPMutationParser extends CirMutationParser {

	@Override
	public void parse(CirTree cir_tree, AstMutation source, Collection<CirMutation> targets) throws Exception {
		AstNode ast_location = source.get_location();
		int loop_time = ((Integer) source.get_parameter()).intValue();
		CirIfStatement if_statement = (CirIfStatement) this.
				get_cir_node(cir_tree, ast_location, CirIfStatement.class, 0);
		CirExecution if_execution = this.get_cir_execution(if_statement);
		for(CirExecutionFlow flow : if_execution.get_ou_flows()) {
			if(flow.get_type() == CirExecutionFlowType.true_flow) {
				CirStatement next = flow.get_target().get_statement();
				targets.add(CirMutations.TTRP(next, loop_time));
			}
		}
	}

}
