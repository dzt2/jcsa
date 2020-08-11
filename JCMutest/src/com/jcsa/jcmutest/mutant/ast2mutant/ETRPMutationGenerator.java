package com.jcsa.jcmutest.mutant.ast2mutant;

import java.util.List;

import com.jcsa.jcmutest.mutant.AstMutation;
import com.jcsa.jcmutest.mutant.AstMutations;
import com.jcsa.jcparse.lang.astree.AstNode;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.unit.AstFunctionDefinition;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;

/**
 * 	trap_on_expression(expression);
 * 	
 * 	@author yukimula
 *
 */
public class ETRPMutationGenerator extends MutationGenerator {
	
	@Override
	protected void initialize(AstFunctionDefinition function) throws Exception { }
	
	@Override
	protected boolean available(AstNode location) throws Exception {
		return this.is_numeric_expression(location)
				&& !this.is_left_reference(location);
	}
	
	@Override
	protected void generate(AstNode location, List<AstMutation> mutations) throws Exception {
		AstExpression expression = (AstExpression) location;
		expression = CTypeAnalyzer.get_expression_of(expression);
		mutations.add(AstMutations.trap_on_expression(expression));
	}

}
