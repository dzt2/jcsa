package com.jcsa.jcmutest.mutant.rip2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutations;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

public class TTRPMutationExtender extends MutationExtender {

	@Override
	protected AstMutation coverage_mutation(AstMutation source) throws Exception {
		return this.coverage_at(source.get_location());
	}

	@Override
	protected AstMutation weak_mutation(AstMutation source) throws Exception {
		AstStatement statement = (AstStatement) source.get_location();
		int loop_time = ((Integer) source.get_parameter()).intValue();
		return AstMutations.trap_for_time(statement, loop_time);
	}

	@Override
	protected AstMutation strong_mutation(AstMutation source) throws Exception {
		return this.weak_mutation(source);
	}

}
