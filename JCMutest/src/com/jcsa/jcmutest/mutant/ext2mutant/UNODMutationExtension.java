package com.jcsa.jcmutest.mutant.ext2mutant;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class UNODMutationExtension extends MutationExtension {

	@Override
	protected AstMutation cover(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		return this.coverage_mutation(expression);
	}

	@Override
	protected AstMutation weak(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		switch(source.get_operator()) {
		case delete_arith_neg:	return AstMutations.trap_on_true(expression);
		case delete_bitws_rsv:	return this.coverage_mutation(expression);
		case delete_logic_not:	return this.coverage_mutation(expression);
		default: throw new IllegalArgumentException(source.toString());
		}
	}

	@Override
	protected AstMutation strong(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		switch(source.get_operator()) {
		case delete_arith_neg:
			return AstMutations.UNOI(expression, COperator.negative);
		case delete_bitws_rsv:
			return AstMutations.UNOI(expression, COperator.bit_not);
		case delete_logic_not:
			return AstMutations.UNOI(expression, COperator.logic_not);
		default: throw new IllegalArgumentException(source.toString());
		}
	}

}
