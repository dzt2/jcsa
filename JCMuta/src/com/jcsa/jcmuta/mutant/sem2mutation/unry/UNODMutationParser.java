package com.jcsa.jcmuta.mutant.sem2mutation.unry;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.SemanticMutationUtil;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstUnaryExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class UNODMutationParser extends SemanticMutationParser {
	
	private AstUnaryExpression get_location(AstMutation ast_mutation) throws Exception {
		return (AstUnaryExpression) CTypeAnalyzer.
				get_expression_of((AstExpression) ast_mutation.get_location());
	}
	/**
	 * get the expression representing the result of the expression being mutated
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private CirExpression get_expression(AstMutation ast_mutation) throws Exception {
		return this.get_result(this.get_location(ast_mutation));
	}
	/**
	 * get the expression representing the operand within the expression to replace
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private CirExpression get_operand(AstMutation ast_mutation) throws Exception {
		AstUnaryExpression expression = this.get_location(ast_mutation);
		return this.get_result(CTypeAnalyzer.get_expression_of(expression.get_operand()));
	}
	
	@Override
	protected CirStatement get_statement(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_expression(ast_mutation);
		if(expression != null) return expression.statement_of();
		else return null;
	}
	
	private void delete_arith_neg(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_expression(ast_mutation);
		CirExpression operand = this.get_operand(ast_mutation);
		Object constant = SemanticMutationUtil.get_constant(operand);
		
		if(constant != null) {
			List<SemanticAssertion> state_errors = new ArrayList<SemanticAssertion>();
			
			if(constant instanceof Long) {
				long mut_value = ((Long) constant).longValue();
				long ori_value = -mut_value;
				if(ori_value != mut_value) {
					state_errors.add(sem_mutation.get_assertions().set_value(expression, Long.valueOf(mut_value)));
					state_errors.add(sem_mutation.get_assertions().diff_value(expression, mut_value - ori_value));
				}
			}
			else if(constant instanceof Double) {
				double mut_value = ((Double) constant).doubleValue();
				double ori_value = -mut_value;
				if(ori_value != mut_value) {
					state_errors.add(sem_mutation.get_assertions().set_value(expression, Double.valueOf(mut_value)));
					state_errors.add(sem_mutation.get_assertions().diff_value(expression, mut_value - ori_value));
				}
			}
			else {
				throw new IllegalArgumentException("Invalid constant");
			}
			
			if(!state_errors.isEmpty()) { this.infect(state_errors); }
		} 
		else {
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().not_equals(operand, Long.valueOf(0));
			this.infect(constraint, sem_mutation.get_assertions().neg_value(expression));
		}
	}
	
	private void delete_bitws_rsv(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_expression(ast_mutation);
		CirExpression operand = this.get_operand(ast_mutation);
		Object constant = SemanticMutationUtil.get_constant(operand);
		
		if(constant != null) {
			List<SemanticAssertion> state_errors = new ArrayList<SemanticAssertion>();
			
			if(constant instanceof Long) {
				long mut_value = ((Long) constant).longValue();
				long ori_value = ~mut_value;
				if(ori_value != mut_value) {
					state_errors.add(sem_mutation.get_assertions().set_value(expression, Long.valueOf(mut_value)));
					state_errors.add(sem_mutation.get_assertions().diff_value(expression, mut_value - ori_value));
				}
			}
			else {
				throw new IllegalArgumentException("Invalid constant");
			}
			
			if(!state_errors.isEmpty()) { this.infect(state_errors); }
		}
		else {
			this.infect(sem_mutation.get_assertions().rsv_value(expression));
		}
	}
	
	private void delete_logic_not(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_expression(ast_mutation);
		CirExpression operand = this.get_operand(ast_mutation);
		List<SemanticAssertion> state_errors = new ArrayList<SemanticAssertion>();
		Object constant = SemanticMutationUtil.get_constant(operand);
		
		if(constant != null) {
			Boolean muta_value;
			
			if(constant instanceof Boolean) {
				muta_value = Boolean.valueOf(((Boolean) constant).booleanValue());
			}
			else if(constant instanceof Double) {
				muta_value = Boolean.valueOf(((Double) constant).doubleValue() != 0);
			}
			else if(constant instanceof Long) {
				muta_value = Boolean.valueOf(((Long) constant).longValue() != 0);
			}
			else throw new IllegalArgumentException("Invalid constant");
			
			state_errors.add(sem_mutation.get_assertions().set_value(expression, muta_value));
			this.infect(state_errors);
		}
		else {
			this.infect(sem_mutation.get_assertions().not_value(expression));
		}
	}
	
	@Override
	protected void generate_infections(AstMutation ast_mutation) throws Exception {
		switch(ast_mutation.get_mutation_operator()) {
		case delete_arith_neg:	this.delete_arith_neg(ast_mutation); break;
		case delete_bitws_rsv:	this.delete_bitws_rsv(ast_mutation); break;
		case delete_logic_not:	this.delete_logic_not(ast_mutation); break;
		default: throw new IllegalArgumentException("Invalid mutation operator.");
		}
	}

}
