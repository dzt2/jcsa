package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.mutation.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;

public class UIOIMutationGenerator extends MutationGenerator {

	@Override
	protected void initialize(AstFunctionDefinition function, Iterable<AstNode> locations) throws Exception { }

	@Override
	protected boolean available(AstNode location) throws Exception {
		return this.is_numeric_expression(location)
				&& !this.is_const_type(location)
				&& !this.is_left_reference(location)
				&& this.is_reference_expression(location);
	}

	@Override
	protected void generate(AstNode location, List<AstMutation> mutations) throws Exception {
		AstExpression expression = (AstExpression) location;
		mutations.add(AstMutations.insert_prev_inc(expression));
		mutations.add(AstMutations.insert_prev_dec(expression));
		mutations.add(AstMutations.insert_post_inc(expression));
		mutations.add(AstMutations.insert_post_dec(expression));
	}

}
