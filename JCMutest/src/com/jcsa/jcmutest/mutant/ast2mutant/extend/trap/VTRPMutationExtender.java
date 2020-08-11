package com.jcsa.jcmutest.mutant.ast2mutant.extend.trap;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.extend.AstMutationExtender;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

public class VTRPMutationExtender extends AstMutationExtender {

	@Override
	public AstMutation coverage_mutation(AstMutation mutation) throws Exception {
		AstExpression expression = this.get_expression_of(mutation);
		return AstMutations.trap_on_expression(expression);
	}

	@Override
	public AstMutation weak_mutation(AstMutation mutation) throws Exception {
		AstExpression expression = this.get_expression_of(mutation);
		switch(mutation.get_operator()) {
		case trap_on_pos:	return AstMutations.trap_on_pos(expression);
		case trap_on_neg:	return AstMutations.trap_on_neg(expression);
		case trap_on_zro:	return AstMutations.trap_on_zro(expression);
		default: throw new IllegalArgumentException("Invalid: " + mutation.get_operator());
		}
	}

	@Override
	public AstMutation strong_mutation(AstMutation mutation) throws Exception {
		return this.weak_mutation(mutation);
	}

}
