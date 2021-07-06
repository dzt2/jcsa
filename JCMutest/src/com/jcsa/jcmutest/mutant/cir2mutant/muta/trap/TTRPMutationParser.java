package com.jcsa.jcmutest.mutant.cir2mutant.muta.trap;

import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.base.SymCondition;
import com.jcsa.jcmutest.mutant.cir2mutant.base.SymConditions;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;


public class TTRPMutationParser extends CirMutationParser {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return (CirStatement) this.get_cir_node(
				cir_tree, mutation.get_location(), CirIfStatement.class);
	}

	@Override
	protected void generate_infections(CirTree cir_tree, CirStatement statement,
			AstMutation mutation, Map<SymCondition, SymCondition> infections) throws Exception {
		/* find the conditional statement of looping block */
		CirExecution condition_execution = SymConditions.execution_of(statement);
		CirExecution branch_head = null;
		for(CirExecutionFlow flow : condition_execution.get_ou_flows()) {
			if(flow.get_type() == CirExecutionFlowType.true_flow) {
				branch_head = flow.get_target(); break;
			}
		}
		
		/* derive running times parameter in TTRP operator */
		int times = ((Integer) mutation.get_parameter()).intValue();
		
		/* construct infection-state-error pairs in mapping */
		SymCondition constraint = SymConditions.cov_stmt(branch_head, times);
		SymCondition init_error = SymConditions.trp_exec(branch_head);
		infections.put(init_error, constraint);
	}
	
}
