package com.jcsa.jcmutest.mutant.ext2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutations;
import com.jcsa.jcparse.lang.astree.stmt.AstBreakStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstContinueStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;

public class SBCRMutationExtension extends MutationExtension {

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
		switch(source.get_operator()) {
		case break_to_continue:
			return AstMutations.break_to_continue((AstBreakStatement) statement);
		case continue_to_break:
			return AstMutations.continue_to_break((AstContinueStatement) statement);
		default: throw new IllegalArgumentException(source.toString());
		}
	}

}
