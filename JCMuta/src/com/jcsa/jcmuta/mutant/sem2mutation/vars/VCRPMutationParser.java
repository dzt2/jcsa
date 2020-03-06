package com.jcsa.jcmuta.mutant.sem2mutation.vars;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.SemanticMutationUtil;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.CConstant;

public class VCRPMutationParser extends SemanticMutationParser {

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
		CConstant constant = (CConstant) ast_mutation.get_parameter();
		Object orig_value = SemanticMutationUtil.get_constant(expression);
		Object muta_value = SemanticMutationUtil.get_const_value(constant);
		
		if(orig_value != null) {
			List<SemanticAssertion> state_errors = new ArrayList<SemanticAssertion>();
			
			if(muta_value instanceof Long && orig_value instanceof Long) {
				long ori_value = ((Long) orig_value).longValue();
				long mut_value = ((Long) muta_value).longValue();
				if(ori_value != mut_value) {
					long difference = mut_value - ori_value;
					state_errors.add(sem_mutation.get_assertions().diff_value(expression, difference));
					state_errors.add(sem_mutation.get_assertions().set_value(expression, (Long) muta_value));
				}
			}
			else if(muta_value instanceof Double && orig_value instanceof Double) {
				double ori_value = ((Double) orig_value).doubleValue();
				double mut_value = ((Double) muta_value).doubleValue();
				if(ori_value != mut_value) {
					double difference = mut_value - ori_value;
					state_errors.add(sem_mutation.get_assertions().diff_value(expression, difference));
					state_errors.add(sem_mutation.get_assertions().set_value(expression, (Double) muta_value));
				}
			}
			else {
				throw new IllegalArgumentException(orig_value + " and " + muta_value);
			}
			
			if(!state_errors.isEmpty()) { this.infect(state_errors); }
		}
		else {
			this.infect(
					sem_mutation.get_assertions().not_equals(expression, muta_value), 
					sem_mutation.get_assertions().mut_value(expression));
		}
	}

}
