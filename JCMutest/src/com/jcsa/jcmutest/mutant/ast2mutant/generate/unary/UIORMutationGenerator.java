package com.jcsa.jcmutest.mutant.ast2mutant.generate.unary;

import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.AstMutationGenerator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncrePostfixExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstIncreUnaryExpression;

public class UIORMutationGenerator extends AstMutationGenerator {

	@Override
	protected boolean is_available(AstNode location) throws Exception {
		return location instanceof AstIncreUnaryExpression
				|| location instanceof AstIncrePostfixExpression;
	}

	@Override
	protected void generate_mutations(AstNode location, List<AstMutation> mutations) throws Exception {
		if(location instanceof AstIncreUnaryExpression) {
			AstIncreUnaryExpression expression = (AstIncreUnaryExpression) location;
			switch(expression.get_operator().get_operator()) {
			case increment:
			{
				mutations.add(AstMutations.prev_inc_to_post_dec(expression));
				mutations.add(AstMutations.prev_inc_to_post_inc(expression));
				mutations.add(AstMutations.prev_inc_to_prev_dec(expression));
				break;
			}
			case decrement:
			{
				mutations.add(AstMutations.prev_dec_to_post_dec(expression));
				mutations.add(AstMutations.prev_dec_to_post_inc(expression));
				mutations.add(AstMutations.prev_dec_to_prev_inc(expression));
				break;
			}
			default: break;
			}
		}
		else {
			AstIncrePostfixExpression expression = (AstIncrePostfixExpression) location;
			switch(expression.get_operator().get_operator()) {
			case increment:
			{
				mutations.add(AstMutations.post_inc_to_post_dec(expression));
				mutations.add(AstMutations.post_inc_to_prev_dec(expression));
				mutations.add(AstMutations.post_inc_to_prev_inc(expression));
				break;
			}
			case decrement:
			{
				mutations.add(AstMutations.post_dec_to_post_inc(expression));
				mutations.add(AstMutations.post_dec_to_prev_dec(expression));
				mutations.add(AstMutations.post_dec_to_prev_inc(expression));
				break;
			}
			default: break;
			}
		}
	}

}
