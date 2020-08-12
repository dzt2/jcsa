package com.jcsa.jcmutest.mutant.ext2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutations;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class RTRPMutationExtension extends MutationExtension {
	
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
		return AstMutations.trap_on_dif(expression, source.get_parameter());
	}
	
	@Override
	protected AstMutation strong(AstMutation source) throws Exception {
		return source;
	}
	
}
