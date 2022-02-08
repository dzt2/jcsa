package com.jcsa.jcmutest.mutant.cir2mutant.muta.trap;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConditionState;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlow;
import com.jcsa.jcparse.lang.irlang.graph.CirExecutionFlowType;
import com.jcsa.jcparse.lang.irlang.stmt.CirIfStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class TTRPCirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement find_reach_point(AstMutation mutation) throws Exception {
		AstNode statement = mutation.get_location();
		return (CirStatement) this.get_cir_node(statement, CirIfStatement.class);
	}

	@Override
	protected void generate_infections(AstMutation mutation) throws Exception {
		/* determine the location for being executed in loops */
		CirExecution execution = this.get_r_execution(), tstmt = null;
		int times = ((Integer) mutation.get_parameter()).intValue();
		for(CirExecutionFlow flow : execution.get_ou_flows()) {
			if(flow.get_type() == CirExecutionFlowType.true_flow) {
				tstmt = flow.get_target(); 
				break;
			}
		}
		
		/* generate the constraint-error infection pairs on mutation */
		CirConditionState constraint = CirAbstractState.cov_time(tstmt, times);
		CirAbstErrorState init_error = CirAbstractState.trp_stmt(tstmt);
		this.put_infection_pair(constraint, init_error);
	}

}
