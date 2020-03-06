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

public class SRTRMutationParser extends SemanticMutationParser {
	
	/**
	 * get the expression representing the original returning value
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private CirExpression get_source_expression(AstMutation ast_mutation) throws Exception {
		AstExpression expression = CTypeAnalyzer.
				get_expression_of((AstExpression) ast_mutation.get_location());
		return this.get_result(expression);
	}
	
	/**
	 * get the expression used to replace the original return value
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private CirExpression get_target_expression(AstMutation ast_mutation) throws Exception {
		AstExpression expression = CTypeAnalyzer.
				get_expression_of((AstExpression) ast_mutation.get_parameter());
		return this.get_result(expression);
	}
	
	@Override
	protected CirStatement get_statement(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_source_expression(ast_mutation);
		if(expression != null) return expression.statement_of();
		else return null;
	}

	@Override
	protected void generate_infections(AstMutation ast_mutation) throws Exception {
		CirExpression source_expression = this.get_source_expression(ast_mutation);
		CirExpression target_expression = this.get_target_expression(ast_mutation);
		List<SemanticAssertion> state_errors = new ArrayList<SemanticAssertion>();
		Object source_constant = SemanticMutationUtil.get_constant(source_expression);
		Object target_constant = SemanticMutationUtil.get_constant(target_expression);
		
		if(source_constant != null) {
			/** (const, const) ==> set_value, diff_value, mut_value **/
			if(target_constant != null) {
				if(source_constant instanceof Long) {
					long source_value = ((Long) source_constant).longValue();
					if(target_constant instanceof Long) {
						long target_value = ((Long) target_constant).longValue();
						
						if(source_value != target_value) {
							state_errors.add(sem_mutation.get_assertions().set_value(source_expression, target_value));
							state_errors.add(sem_mutation.get_assertions().diff_value(source_expression, target_value - source_value));
						}
					}
					else if(target_constant instanceof Double) {
						double target_value = ((Double) target_constant).doubleValue();
						if(source_value != target_value) {
							state_errors.add(sem_mutation.get_assertions().set_value(source_expression, target_value));
							state_errors.add(sem_mutation.get_assertions().diff_value(source_expression, target_value - source_value));
						}
					}
					else {
						throw new IllegalArgumentException("Invalid target_constant");
					}
				}
				else if(source_constant instanceof Double) {
					double source_value = ((Double) source_constant).doubleValue();
					if(target_constant instanceof Long) {
						long target_value = ((Long) target_constant).longValue();
						
						if(source_value != target_value) {
							state_errors.add(sem_mutation.get_assertions().set_value(source_expression, target_value));
							state_errors.add(sem_mutation.get_assertions().diff_value(source_expression, target_value - source_value));
						}
					}
					else if(target_constant instanceof Double) {
						double target_value = ((Double) target_constant).doubleValue();
						if(source_value != target_value) {
							state_errors.add(sem_mutation.get_assertions().set_value(source_expression, target_value));
							state_errors.add(sem_mutation.get_assertions().diff_value(source_expression, target_value - source_value));
						}
					}
					else {
						throw new IllegalArgumentException("Invalid target_constant");
					}
				}
				else {
					throw new IllegalArgumentException("Invalid source_constant");
				}
				
				if(!state_errors.isEmpty()) this.infect(state_errors);
			}
			/** const, expr ==> mut_value, inc_value | dec_value **/
			else {
				SemanticAssertion constraint;
				constraint = sem_mutation.get_assertions().not_equals(target_expression, source_constant);
				this.infect(constraint, sem_mutation.get_assertions().mut_value(source_expression));
			}
		}
		else {
			/** expr, const ==> mut_value, inc_value | dec_value **/
			if(target_constant != null) {
				SemanticAssertion constraint;
				constraint = sem_mutation.get_assertions().not_equals(source_expression, target_constant);
				this.infect(constraint, sem_mutation.get_assertions().mut_value(source_expression));
			}
			/** expr, expr ==> mut_value **/
			else {
				this.infect(sem_mutation.get_assertions().mut_value(source_expression));
			}
		}
	}

}
