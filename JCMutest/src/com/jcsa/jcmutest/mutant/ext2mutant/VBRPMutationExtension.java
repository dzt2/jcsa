package com.jcsa.jcmutest.mutant.ext2mutant;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class VBRPMutationExtension extends MutationExtension {

	@Override
	protected AstMutation cover(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		expression = CTypeAnalyzer.get_expression_of(expression);
		return this.coverage_mutation(expression);
	}

	@Override
	protected AstMutation weak(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		expression = CTypeAnalyzer.get_expression_of(expression);
		switch(source.get_operator()) {
		case set_true:	return AstMutations.trap_on_false(expression);
		case set_false:	return AstMutations.trap_on_true(expression);
		default: throw new IllegalArgumentException(source.toString());
		}
	}

	@Override
	protected AstMutation strong(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		expression = CTypeAnalyzer.get_expression_of(expression);
		switch(source.get_operator()) {
		case set_true:	return AstMutations.VBRP(expression, true);
		case set_false:	return AstMutations.VBRP(expression, false);
		default: throw new IllegalArgumentException(source.toString());
		}
	}

}
