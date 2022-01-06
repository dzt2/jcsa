package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

public class VBRPMutationGenerator extends MutationGenerator {

	@Override
	protected void initialize(AstFunctionDefinition function, Iterable<AstNode> locations) throws Exception {}

	@Override
	protected boolean available(AstNode location) throws Exception {
		return this.is_condition_expression(location);
	}

	@Override
	protected void generate(AstNode location, List<AstMutation> mutations) throws Exception {
		AstExpression expression = (AstExpression) location;
		expression = CTypeAnalyzer.get_expression_of(expression);
		mutations.add(AstMutations.VBRP(expression, true));
		mutations.add(AstMutations.VBRP(expression, false));
	}

}
