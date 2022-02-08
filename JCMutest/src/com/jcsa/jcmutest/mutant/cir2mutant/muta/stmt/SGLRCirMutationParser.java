package com.jcsa.jcmutest.mutant.cir2mutant.muta.stmt;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConditionState;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class SGLRCirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement find_reach_point(AstMutation mutation) throws Exception {
		return this.get_beg_statement(mutation.get_location());
	}

	@Override
	protected void generate_infections(AstMutation mutation) throws Exception {
		/* source and original flow */
		CirExecution source = this.get_r_execution();
		CirExecutionFlow orig_flow = source.get_ou_flow(0);
		CirExecution orig_target = orig_flow.get_target();
		
		/* mutated target and flow */
		AstNode parameter = (AstNode) mutation.get_parameter();
		CirStatement statement = this.get_beg_statement(parameter);
		CirExecution muta_target = statement.execution_of();
		
		/* generate constriant-error infection */
		if(orig_target != muta_target) {
			CirExecutionFlow muta_flow = CirExecutionFlow.virtual_flow(source, muta_target);
			CirConditionState constriant = CirAbstractState.cov_time(source, 1);
			CirAbstErrorState init_error = CirAbstractState.mut_flow(orig_flow, muta_flow);
			this.put_infection_pair(constriant, init_error);
		}
	}

}
