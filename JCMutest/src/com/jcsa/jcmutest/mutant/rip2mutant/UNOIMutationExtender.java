package com.jcsa.jcmutest.mutant.rip2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutations;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.mutant.mutation.MutaGroup;
import com.jcsa.jcmutest.mutant.mutation.MutaOperator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class UNOIMutationExtender extends MutationExtender {

	@Override
	protected AstMutation coverage_mutation(AstMutation source) throws Exception {
		return this.coverage_at(source.get_location());
	}

	@Override
	protected AstMutation weak_mutation(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		expression = CTypeAnalyzer.get_expression_of(expression);
		
		switch(source.get_operator()) {
		case insert_arith_neg:
			return AstMutations.new_mutation(MutaGroup.Trapping_Mutation, 
					MutaClass.VTRP, MutaOperator.trap_on_nzro, 
					expression, null);
		case insert_bitws_rsv:
		case insert_logic_not:
			return this.coverage_at(expression);
		case insert_abs_value:
			return AstMutations.trap_on_neg(expression);
		case insert_nabs_value:
			return AstMutations.trap_on_pos(expression);
		default: throw new IllegalArgumentException("Unknown: " + source);
		}
	}

	@Override
	protected AstMutation strong_mutation(AstMutation source) throws Exception {
		return source;
	}

}
