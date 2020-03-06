package com.jcsa.jcmuta.mutant.sem2mutation.trap;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.SemanticMutationUtil;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.base.AstConstant;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class CTRPMutationParser extends SemanticMutationParser {
	
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
	 * extract the constant parameter from the mutation
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private Long get_parameter(AstMutation ast_mutation) throws Exception {
		AstExpression expression = (AstExpression) ast_mutation.get_parameter();
		AstConstant constant = (AstConstant) CTypeAnalyzer.get_expression_of(expression);
		return (Long) SemanticMutationUtil.get_const_value(constant.get_constant());
	}
	
	@Override
	protected CirStatement get_statement(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_result(this.get_location(ast_mutation));
		if(expression != null) return expression.statement_of();
		else return null;
	}
	
	@Override
	protected void generate_infections(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_result(this.get_location(ast_mutation));
		Long parameter = this.get_parameter(ast_mutation);
		SemanticAssertion constraint, state_error;
		Long constant = (Long) SemanticMutationUtil.get_constant(expression);
		
		if(constant != null) {
			if(constant.longValue() == parameter.longValue()) {
				state_error = sem_mutation.get_assertions().trapping();
				this.infect(state_error);
			}
			else {
				return;		/** equivalent mutant without state errors **/
			}
		}
		else {
			constraint = sem_mutation.get_assertions().equal_with(expression, parameter);
			state_error = sem_mutation.get_assertions().trapping();
			this.infect(constraint, state_error);
		}
	}
	
}
