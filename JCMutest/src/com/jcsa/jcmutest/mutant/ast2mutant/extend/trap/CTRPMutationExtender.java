package com.jcsa.jcmutest.mutant.ast2mutant.extend.trap;

import com.jcsa.jcmutest.mutant.MutaClass;
import com.jcsa.jcmutest.mutant.MutaGroup;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.extend.AstMutationExtender;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

public class CTRPMutationExtender extends AstMutationExtender {

	@Override
	public AstMutation coverage_mutation(AstMutation mutation) throws Exception {
		AstExpression expression = this.get_expression_of(mutation);
		return AstMutations.trap_on_expression(expression);
	}

	@Override
	public AstMutation weak_mutation(AstMutation mutation) throws Exception {
		AstExpression expression = this.get_expression_of(mutation);
		return AstMutations.new_mutation(MutaGroup.Trapping_Mutation, 
				MutaClass.CTRP, MutaOperator.trap_on_case, 
				expression, mutation.get_parameter());
	}

	@Override
	public AstMutation strong_mutation(AstMutation mutation) throws Exception {
		return this.weak_mutation(mutation);
	}

}
