package com.jcsa.jcmuta.mutant.sem2mutation.vars;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirReferExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.scope.CName;

public class VRRPMutationParser extends SemanticMutationParser {

	/**
	 * get the location that the trapping really occurs.
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private AstExpression get_location(AstMutation ast_mutation) throws Exception {
		AstExpression expression = (AstExpression) ast_mutation.get_location();
		return CTypeAnalyzer.get_expression_of(expression);
	}
	
	/**
	 * get the expression representing the AST mutation
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private CirExpression get_cir_result(AstMutation ast_mutation) throws Exception {
		return this.get_result(this.get_location(ast_mutation));
	}
	
	@Override
	protected CirStatement get_statement(AstMutation ast_mutation) throws Exception {
		CirExpression expression = get_cir_result(ast_mutation);
		if(expression != null) return expression.statement_of();
		else return null;
	}
	
	@Override
	protected void generate_infections(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_cir_result(ast_mutation);
		CName parameter = (CName) ast_mutation.get_parameter();
		SemanticAssertion constraint = sem_mutation.get_assertions().not_equals(expression, parameter.get_name());
		if(expression instanceof CirReferExpression)
			infect(constraint, sem_mutation.get_assertions().mut_refer((CirReferExpression) expression));
		else 
			infect(constraint, sem_mutation.get_assertions().mut_value(expression));
	}

}
