package com.jcsa.jcmuta.mutant.sem2mutation.unry;

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

public class VINCMutationParser extends SemanticMutationParser {
	
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
		/*
		CirExpression expression = get_cir_result(ast_mutation);
		if(expression != null) return expression.statement_of();
		else return null;
		*/
		return this.get_beg_statement(ast_mutation.get_location());
	}
	
	private void inc_value(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_cir_result(ast_mutation);
		List<SemanticAssertion> state_errors = new ArrayList<SemanticAssertion>();
		int parameter = (int) ast_mutation.get_parameter();
		Object constant = SemanticMutationUtil.get_constant(expression);
		
		if(constant != null) {
			if(constant instanceof Long) {
				long ori_value = ((Long) constant).longValue();
				long mut_value = ori_value + parameter;
				state_errors.add(sem_mutation.get_assertions().diff_value(expression, parameter));
				state_errors.add(sem_mutation.get_assertions().set_value(expression, Long.valueOf(mut_value)));
			}
			else throw new IllegalArgumentException("Invalid constant: null");
		}
		else {
			state_errors.add(sem_mutation.get_assertions().diff_value(expression, parameter));
		}
		
		this.infect(state_errors);
	}
	
	private void mul_value(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_cir_result(ast_mutation);
		List<SemanticAssertion> state_errors = new ArrayList<SemanticAssertion>();
		double parameter = (double) ast_mutation.get_parameter();
		Object constant = SemanticMutationUtil.get_constant(expression);
		
		if(constant != null) {
			if(constant instanceof Double) {
				double ori_value = ((Double) constant).doubleValue();
				double mut_value = ori_value * parameter;
				if(mut_value == ori_value) return;
				state_errors.add(sem_mutation.get_assertions().set_value(expression, Double.valueOf(mut_value)));
				state_errors.add(sem_mutation.get_assertions().diff_value(expression, mut_value - ori_value));
			}
			else {
				throw new IllegalArgumentException("Invalid constant");
			}
			if(!state_errors.isEmpty()) this.infect(state_errors);
		}
		else {
			SemanticAssertion constraint, error1, error2;
			error1 = sem_mutation.get_assertions().inc_value(expression);
			error2 = sem_mutation.get_assertions().dec_value(expression);
			
			if(parameter > 1) {
				constraint = sem_mutation.get_assertions().greater_tn(expression, Long.valueOf(0));
				this.infect(constraint, error1);
				
				constraint = sem_mutation.get_assertions().smaller_tn(expression, Long.valueOf(0));
				this.infect(constraint, error2);
			}
			else {
				constraint = sem_mutation.get_assertions().greater_tn(expression, Long.valueOf(0));
				this.infect(constraint, error2);
				
				constraint = sem_mutation.get_assertions().smaller_tn(expression, Long.valueOf(0));
				this.infect(constraint, error1);
			}
			
			this.infect(sem_mutation.get_assertions().mut_value(expression));
		}
	}
	
	@Override
	protected void generate_infections(AstMutation ast_mutation) throws Exception {
		switch(ast_mutation.get_mutation_operator()) {
		case inc_value: this.inc_value(ast_mutation); break;
		case mul_value: this.mul_value(ast_mutation); break;
		default: throw new IllegalArgumentException("Invalid mutation operator.");
		}
	}

}
