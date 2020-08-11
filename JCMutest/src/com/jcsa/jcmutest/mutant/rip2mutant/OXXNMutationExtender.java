package com.jcsa.jcmutest.mutant.rip2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutations;
import com.jcsa.jcmutest.mutant.mutation.MutaOperator;

public class OXXNMutationExtender extends MutationExtender {

	@Override
	protected AstMutation coverage_mutation(AstMutation source) throws Exception {
		return this.coverage_at(source.get_location());
	}

	@Override
	protected AstMutation weak_mutation(AstMutation source) throws Exception {
		return AstMutations.new_mutation(source.get_group(), 
				source.get_class(), MutaOperator.cmp_operator, 
				source.get_location(), source.get_parameter());
	}

	@Override
	protected AstMutation strong_mutation(AstMutation source) throws Exception {
		return source;
	}

}
