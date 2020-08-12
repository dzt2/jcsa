package com.jcsa.jcmutest.mutant.ext2mutant;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutations;
import com.jcsa.jcmutest.mutant.mutation.MutaOperator;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OXXNMutationExtension extends MutationExtension {

	@Override
	protected AstMutation cover(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		return this.coverage_mutation(expression);
	}

	@Override
	protected AstMutation weak(AstMutation source) throws Exception {
		AstExpression expression = (AstExpression) source.get_location();
		COperator operator = (COperator) source.get_parameter();
		return AstMutations.new_mutation(source.get_group(), source.
				get_class(), MutaOperator.cmp_operator, expression, operator);
	}

	@Override
	protected AstMutation strong(AstMutation source) throws Exception {
		return source;
	}

}
