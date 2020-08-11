package com.jcsa.jcmutest.mutant.ast2mutant.extend.trap;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.extend.AstMutationExtender;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

public class BTRPMutationExtender extends AstMutationExtender {

	@Override
	public AstMutation coverage_mutation(AstMutation mutation) throws Exception {
		AstExpression expression = this.get_expression_of(mutation);
		return AstMutations.trap_on_expression(expression);
	}

	@Override
	public AstMutation weak_mutation(AstMutation mutation) throws Exception {
		AstExpression expression = this.get_expression_of(mutation);
		switch(mutation.get_operator()) {
		case trap_on_true:	return AstMutations.trap_on_true(expression);
		case trap_on_false:	return AstMutations.trap_on_false(expression);
		default: throw new IllegalArgumentException("Invalid: " + mutation.get_operator());
		}
	}

	@Override
	public AstMutation strong_mutation(AstMutation mutation) throws Exception {
		return this.weak_mutation(mutation);
	}

}
