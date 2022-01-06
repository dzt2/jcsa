package com.jcsa.jcmutest.mutant.ext2mutant;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcmutest.mutant.MutaClass;
import com.jcsa.jcmutest.mutant.MutaGroup;
import com.jcsa.jcmutest.mutant.MutaOperator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

/**
 * trap_on_expression(e)
 * trap_on_case(e, c)
 * trap_on_case(e, c)
 *
 * @author yukimula
 *
 */
public class CTRPMutationExtension extends MutationExtension {

	@Override
	protected AstMutation cover(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		return this.coverage_mutation(expression);
	}

	@Override
	protected AstMutation weak(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		AstExpression parameter = (AstExpression) source.get_parameter();
		return AstMutations.new_mutation(MutaGroup.Trapping_Mutation,
				MutaClass.CTRP, MutaOperator.trap_on_case,
				CTypeAnalyzer.get_expression_of(expression),
				CTypeAnalyzer.get_expression_of(parameter));
	}

	@Override
	protected AstMutation strong(AstMutation source) throws Exception {
		return this.weak(source);
	}

}
