package com.jcsa.jcmutest.mutant.ast2mutant.generate.refs;

import java.util.List;

import com.jcsa.jcmutest.mutant.ast2mutant.AstMutation;
import com.jcsa.jcmutest.mutant.ast2mutant.AstMutations;
import com.jcsa.jcmutest.mutant.ast2mutant.generate.AstMutationGenerator;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;

public class VBRPMutationGenerator extends AstMutationGenerator {

	@Override
	protected boolean is_available(AstNode location) throws Exception {
		return this.is_condition_expression(location);
	}

	@Override
	protected void generate_mutations(AstNode location, List<AstMutation> mutations) throws Exception {
		AstExpression expression = (AstExpression) location;
		mutations.add(AstMutations.VBRP(expression, true));
		mutations.add(AstMutations.VBRP(expression, false));
	}

}
