package com.jcsa.jcmutest.mutant.ext2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutations;
import com.jcsa.jcparse.lang.astree.stmt.AstDoWhileStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstStatement;
import com.jcsa.jcparse.lang.astree.stmt.AstWhileStatement;

public class SWDRMutationExtension extends MutationExtension {

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
		case while_to_do_while:
			return AstMutations.while_to_do_while((AstWhileStatement) statement);
		case do_while_to_while:
			return AstMutations.do_while_to_while((AstDoWhileStatement) statement);
		default: throw new IllegalArgumentException(source.toString());
		}
	}

}
