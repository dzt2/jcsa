package com.jcsa.jcmutest.mutant.cir2mutant;

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
	protected void parse(CirTree tree, AstMutation source, 
			List<CirMutation> targets) throws Exception {
		int loop_time = ((Integer) source.get_parameter()).intValue();
		CirStatement statement = (CirStatement) this.get_cir_nodes(tree, 
					source.get_location(), CirIfStatement.class).get(0);
		CirExecution execution = this.get_cir_execution(statement);
		for(CirExecutionFlow flow : execution.get_ou_flows()) {
			if(flow.get_type() == CirExecutionFlowType.true_flow) {
				targets.add(CirMutations.STRP(flow.get_target().get_statement(), loop_time));
			}
		}
	}

}
