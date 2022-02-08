package com.jcsa.jcmutest.mutant.cir2mutant.muta.trap;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParser;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstErrorState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAbstractState;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirConditionState;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class STRPCirMutationParser extends CirMutationParser {

	@Override
	protected CirStatement find_reach_point(AstMutation mutation) throws Exception {
		return this.get_beg_statement(mutation.get_location());
	}

	@Override
	protected void generate_infections(AstMutation mutation) throws Exception {
		CirExecution execution = this.get_r_execution();
		CirConditionState constraint = CirAbstractState.cov_time(execution, 1);
		CirAbstErrorState init_error = CirAbstractState.trp_stmt(execution);
		this.put_infection_pair(constraint, init_error);
	}

}
