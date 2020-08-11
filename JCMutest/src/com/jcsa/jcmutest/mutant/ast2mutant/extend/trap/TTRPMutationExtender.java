package com.jcsa.jcmutest.mutant.ast2mutant.extend.trap;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.extend.AstMutationExtender;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

public class TTRPMutationExtender extends AstMutationExtender {
	
	private STRPMutationExtender extender = new STRPMutationExtender();

	@Override
	public AstMutation coverage_mutation(AstMutation mutation) throws Exception {
		AstStatement statement = (AstStatement) mutation.get_location();
		return this.extender.coverage_mutation(AstMutations.trap_on_statement(statement));
	}

	@Override
	public AstMutation weak_mutation(AstMutation mutation) throws Exception {
		AstStatement statement = (AstStatement) mutation.get_location();
		int loop_time = ((Integer) mutation.get_parameter()).intValue();
		return AstMutations.trap_for_time(statement, loop_time);
	}

	@Override
	public AstMutation strong_mutation(AstMutation mutation) throws Exception {
		return this.weak_mutation(mutation);
	}

}
