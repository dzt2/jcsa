package com.jcsa.jcmutest.mutant.rip2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutations;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.mutant.mutation.MutaGroup;
import com.jcsa.jcmutest.mutant.mutation.MutaOperator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

/**
 * trap_on_expr(e); trap_on_case(e, c); trap_on_case(e, c);
 * 
 * @author dzt2
 *
 */
public class CTRPMutationExtender extends MutationExtender {

	@Override
	protected AstMutation coverage_mutation(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		return this.coverage_at(expression);
	}

	@Override
	protected AstMutation weak_mutation(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		AstExpression case_expression = (AstExpression) source.get_parameter();
		return AstMutations.new_mutation(MutaGroup.Trapping_Mutation, 
				MutaClass.CTRP, MutaOperator.trap_on_case, 
				expression, case_expression);
	}

	@Override
	protected AstMutation strong_mutation(AstMutation source) throws Exception {
		return this.weak_mutation(source);
	}

}
