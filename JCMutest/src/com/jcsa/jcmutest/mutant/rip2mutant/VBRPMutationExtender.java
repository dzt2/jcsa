package com.jcsa.jcmutest.mutant.rip2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutations;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class VBRPMutationExtender extends MutationExtender {

	@Override
	protected AstMutation coverage_mutation(AstMutation source) throws Exception {
		return this.coverage_at(source.get_location());
	}

	@Override
	protected AstMutation weak_mutation(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		expression = CTypeAnalyzer.get_expression_of(expression);
		
		switch(source.get_operator()) {
		case set_true:	return AstMutations.trap_on_false(expression);
		case set_false:	return AstMutations.trap_on_true(expression);
		default: throw new IllegalArgumentException("Unsupport: " + source);
		}
	}

	@Override
	protected AstMutation strong_mutation(AstMutation source) throws Exception {
		return source;
	}

}
