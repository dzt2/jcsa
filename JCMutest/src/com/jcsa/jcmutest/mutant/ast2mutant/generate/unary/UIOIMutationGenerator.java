package com.jcsa.jcmutest.mutant.ast2mutant.generate.unary;

import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.AstMutationGenerator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

public class UIOIMutationGenerator extends AstMutationGenerator {
	
	@Override
	protected boolean is_available(AstNode location) throws Exception {
		return this.is_reference_expression(location) 
				&& !this.is_left_reference(location)
				&& this.is_numeric_expression(location);
	}
	
	@Override
	protected void generate_mutations(AstNode location, List<AstMutation> mutations) throws Exception {
		AstExpression reference = (AstExpression) location;
		mutations.add(AstMutations.insert_prev_inc(reference));
		mutations.add(AstMutations.insert_prev_dec(reference));
		mutations.add(AstMutations.insert_post_inc(reference));
		mutations.add(AstMutations.insert_post_dec(reference));
	}
	
}
