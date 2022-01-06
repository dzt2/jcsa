package com.jcsa.jcmutest.mutant.ext2mutant;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.lexical.COperator;

public class UNOIMutationExtension extends MutationExtension {

	@Override
	protected AstMutation cover(AstMutation source) throws Exception {
		AstExpression expression =
				(AstExpression) source.get_location();
		return this.coverage_mutation(expression);
	}

	@Override
	protected AstMutation weak(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		expression = CTypeAnalyzer.get_expression_of(expression);

		switch(source.get_operator()) {
		case insert_arith_neg:	return AstMutations.trap_on_true(expression);
		case insert_bitws_rsv:	return this.coverage_mutation(expression);
		case insert_logic_not:	return this.coverage_mutation(expression);
		case insert_abs_value:	return AstMutations.trap_on_neg(expression);
		case insert_nabs_value:	return AstMutations.trap_on_pos(expression);
		default: throw new IllegalArgumentException(source.toString());
		}
	}

	@Override
	protected AstMutation strong(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		expression = CTypeAnalyzer.get_expression_of(expression);

		switch(source.get_operator()) {
		case insert_arith_neg:	return AstMutations.UNOI(expression, COperator.negative);
		case insert_bitws_rsv:	return AstMutations.UNOI(expression, COperator.bit_not);
		case insert_logic_not:	return AstMutations.UNOI(expression, COperator.logic_not);
		case insert_abs_value:	return AstMutations.UNOI(expression, COperator.positive);
		case insert_nabs_value:	return AstMutations.UNOI(expression, COperator.assign);
		default: throw new IllegalArgumentException(source.toString());
		}
	}

}
