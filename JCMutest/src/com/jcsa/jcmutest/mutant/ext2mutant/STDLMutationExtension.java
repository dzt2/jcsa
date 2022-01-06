package com.jcsa.jcmutest.mutant.ext2mutant;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

public class STDLMutationExtension extends MutationExtension {

	@Override
	protected AstMutation cover(AstMutation source) throws Exception {
		AstStatement statement = (AstStatement) source.get_location();
		return this.coverage_mutation(statement);
	}

	@Override
	protected AstMutation weak(AstMutation source) throws Exception {
		return this.cover(source);
	}

	@Override
	protected AstMutation strong(AstMutation source) throws Exception {
		AstStatement statement = (AstStatement) source.get_location();
		return AstMutations.delete_statement(statement);
	}

}
