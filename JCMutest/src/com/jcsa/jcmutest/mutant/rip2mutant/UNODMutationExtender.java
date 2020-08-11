package com.jcsa.jcmutest.mutant.rip2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutations;
import com.jcsa.jcmutest.mutant.mutation.MutaClass;
import com.jcsa.jcmutest.mutant.mutation.MutaGroup;
import com.jcsa.jcmutest.mutant.mutation.MutaOperator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class UNODMutationExtender extends MutationExtender {

	@Override
	protected AstMutation coverage_mutation(AstMutation source) throws Exception {
		return this.coverage_at(source.get_location());
	}

	@Override
	protected AstMutation weak_mutation(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		switch(source.get_operator()) {
		case delete_arith_neg:
		{
			return AstMutations.new_mutation(MutaGroup.Trapping_Mutation, 
					MutaClass.VTRP, MutaOperator.trap_on_nzro, 
					expression, null);
		}
		case delete_bitws_rsv:
		{
			return this.coverage_at(expression);
		}
		case delete_logic_not:
		{
			return this.coverage_at(expression);
		}
		default: throw new IllegalArgumentException("Unsupport: " + source);
		}
	}

	@Override
	protected AstMutation strong_mutation(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		switch(source.get_operator()) {
		case delete_arith_neg:
			return AstMutations.UNOI(expression, COperator.negative);
		case delete_bitws_rsv:
			return AstMutations.UNOI(expression, COperator.bit_not);
		case delete_logic_not:
			return AstMutations.UNOI(expression, COperator.logic_not);
		default: throw new IllegalArgumentException("Unsupport: " + source);
		}
	}

}
