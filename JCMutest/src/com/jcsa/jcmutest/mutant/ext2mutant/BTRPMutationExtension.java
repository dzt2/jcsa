package com.jcsa.jcmutest.mutant.ext2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutations;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

/**
 * trap_on_expression(e)
 * trap_on_true|false(e)
 * trap_on_true|false(e)
 *
 * @author yukimula
 *
 */
public class BTRPMutationExtension extends MutationExtension {

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
		case trap_on_true:	return AstMutations.trap_on_true(expression);
		case trap_on_false:	return AstMutations.trap_on_false(expression);
		default: throw new IllegalArgumentException("Invalid: " + source);
		}
	}

	@Override
	protected AstMutation strong(AstMutation source) throws Exception {
		return this.weak(source);
	}

}
