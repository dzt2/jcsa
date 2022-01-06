package com.jcsa.jcmutest.mutant.ext2mutant;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

/**
 * trap_on_expression(e)
 * trap_on_pos|neg|zro(e)
 * trap_on_pos|neg|zro(e)
 *
 * @author yukimula
 *
 */
public class VTRPMutationExtension extends MutationExtension {

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
		case trap_on_pos:	return AstMutations.trap_on_pos(expression);
		case trap_on_zro:	return AstMutations.trap_on_zro(expression);
		case trap_on_neg:	return AstMutations.trap_on_neg(expression);
		case trap_on_dif:	return AstMutations.trap_on_dif(
									expression, source.get_parameter());
		default: throw new IllegalArgumentException(source.toString());
		}
	}

	@Override
	protected AstMutation strong(AstMutation source) throws Exception {
		return this.weak(source);
	}

}
