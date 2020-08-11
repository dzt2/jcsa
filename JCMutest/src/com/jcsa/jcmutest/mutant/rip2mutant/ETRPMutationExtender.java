package com.jcsa.jcmutest.mutant.rip2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class ETRPMutationExtender extends MutationExtender {

	@Override
	protected AstMutation coverage_mutation(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		return this.coverage_at(CTypeAnalyzer.get_expression_of(expression));
	}

	@Override
	protected AstMutation weak_mutation(AstMutation source) throws Exception {
		return this.coverage_mutation(source);
	}

	@Override
	protected AstMutation strong_mutation(AstMutation source) throws Exception {
		return this.coverage_mutation(source);
	}

}
