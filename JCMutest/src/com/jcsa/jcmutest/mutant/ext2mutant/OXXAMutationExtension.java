package com.jcsa.jcmutest.mutant.ext2mutant;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

public class OXXAMutationExtension extends MutationExtension {

	@Override
	protected AstMutation cover(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		return this.coverage_mutation(expression);
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
