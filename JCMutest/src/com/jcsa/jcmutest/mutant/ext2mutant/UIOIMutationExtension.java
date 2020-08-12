package com.jcsa.jcmutest.mutant.ext2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;

public class UIOIMutationExtension extends MutationExtension {

	@Override
	protected AstMutation cover(AstMutation source) throws Exception {
		return this.coverage_mutation(source.get_location());
	}

	@Override
	protected AstMutation weak(AstMutation source) throws Exception {
		return this.cover(source);
	}

	@Override
	protected AstMutation strong(AstMutation source) throws Exception {
		return source;
	}

}
