package com.jcsa.jcmuta.mutant.sem2mutation.unry;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.SemanticMutationUtil;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class UNOIMutationParser extends SemanticMutationParser {
	
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
	
	private void insert_arith_neg(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_cir_result(ast_mutation);
		Object constant = SemanticMutationUtil.get_constant(expression);
		
		if(constant != null) {
			List<SemanticAssertion> state_errors = new ArrayList<SemanticAssertion>();
			
			if(constant instanceof Long) {
				long ori_value = ((Long) constant).longValue();
				long mut_value = -ori_value;
				
				if(ori_value != mut_value) {
					state_errors.add(sem_mutation.get_assertions().set_value(expression, Long.valueOf(mut_value)));
					state_errors.add(sem_mutation.get_assertions().diff_value(expression, mut_value - ori_value));
				}
			}
			else if(constant instanceof Double) {
				double ori_value = ((Double) constant).doubleValue();
				double mut_value = -ori_value;
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
			SemanticAssertion constraint = sem_mutation.get_assertions().not_equals(expression, 0);
			this.infect(constraint, sem_mutation.get_assertions().neg_value(expression));
		}
	}
	
	private void insert_bitws_rsv(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_cir_result(ast_mutation);
		Object constant = SemanticMutationUtil.get_constant(expression);
		
		if(constant != null) {
			List<SemanticAssertion> state_errors = new ArrayList<SemanticAssertion>();
			
			if(constant instanceof Long) {
				long ori_value = ((Long) constant).longValue();
				long mut_value = ~ori_value;
				
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
	
	private void insert_logic_not(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_cir_result(ast_mutation);
		Object constant = SemanticMutationUtil.get_constant(expression);
		
		if(constant != null) {
			List<SemanticAssertion> state_errors = new ArrayList<SemanticAssertion>(); Boolean muta_value;
			
			if(constant instanceof Boolean) {
				muta_value = Boolean.valueOf(!((Boolean) constant).booleanValue());
			}
			else if(constant instanceof Long) {
				muta_value = Boolean.valueOf(((Long) constant).longValue() == 0);
			}
			else if(constant instanceof Double) {
				muta_value = Boolean.valueOf(((Double) constant).doubleValue() == 0);
			}
			else {
				throw new IllegalArgumentException("Invalid constant");
			}
			
			state_errors.add(sem_mutation.get_assertions().set_value(expression, muta_value));
			this.infect(state_errors);
		}
		else {
			CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
			SemanticAssertion constraint, state_error;
			
			if(CTypeAnalyzer.is_boolean(data_type)) {
				constraint = sem_mutation.get_assertions().equal_with(expression, Boolean.TRUE);
				state_error = sem_mutation.get_assertions().set_value(expression, Boolean.FALSE);
				infect(constraint, state_error);
				
				constraint = sem_mutation.get_assertions().equal_with(expression, Boolean.FALSE);
				state_error = sem_mutation.get_assertions().set_value(expression, Boolean.TRUE);
				infect(constraint, state_error);
			}
			else if(CTypeAnalyzer.is_number(data_type)) {
				constraint = sem_mutation.get_assertions().not_equals(expression, Long.valueOf(0));
				state_error = sem_mutation.get_assertions().set_value(expression, Boolean.FALSE);
				infect(constraint, state_error);
				
				constraint = sem_mutation.get_assertions().equal_with(expression, Long.valueOf(0));
				state_error = sem_mutation.get_assertions().set_value(expression, Boolean.TRUE);
				infect(constraint, state_error);
			}
			else if(CTypeAnalyzer.is_pointer(data_type)) {
				constraint = sem_mutation.get_assertions().not_equals(expression, Nullptr);
				state_error = sem_mutation.get_assertions().set_value(expression, Boolean.FALSE);
				infect(constraint, state_error);
				
				constraint = sem_mutation.get_assertions().equal_with(expression, Nullptr);
				state_error = sem_mutation.get_assertions().set_value(expression, Boolean.TRUE);
				infect(constraint, state_error);
			}
			else {
				throw new IllegalArgumentException("Invalid data type");
			}
			
			this.infect(sem_mutation.get_assertions().not_value(expression));
		}
	}
	
	private void insert_abs(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_cir_result(ast_mutation);
		Object constant = SemanticMutationUtil.get_constant(expression);
		
		if(constant != null) {
			List<SemanticAssertion> state_errors = new ArrayList<SemanticAssertion>();
			
			if(constant instanceof Long) {
				long ori_value = ((Long) constant).longValue();
				long mut_value = -ori_value;
				if(ori_value < 0) {
					state_errors.add(sem_mutation.get_assertions().set_value(expression, Long.valueOf(mut_value)));
					state_errors.add(sem_mutation.get_assertions().diff_value(expression, mut_value - ori_value));
				}
			}
			else if(constant instanceof Double) {
				double ori_value = ((Double) constant).doubleValue();
				double mut_value = -ori_value;
				if(ori_value < 0) {
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
			SemanticAssertion constraint, error1, error2;
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			error1 = sem_mutation.get_assertions().neg_value(expression);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error1, error2 });
		}
	}
	
	private void insert_neg_abs(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_cir_result(ast_mutation);
		Object constant = SemanticMutationUtil.get_constant(expression);
		
		if(constant != null) {
			List<SemanticAssertion> state_errors = new ArrayList<SemanticAssertion>();
			
			if(constant instanceof Long) {
				long ori_value = ((Long) constant).longValue();
				long mut_value = -ori_value;
				if(ori_value > 0) {
					state_errors.add(sem_mutation.get_assertions().set_value(expression, Long.valueOf(mut_value)));
					state_errors.add(sem_mutation.get_assertions().diff_value(expression, mut_value - ori_value));
				}
			}
			else if(constant instanceof Double) {
				double ori_value = ((Double) constant).doubleValue();
				double mut_value = -ori_value;
				if(ori_value > 0) {
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
			SemanticAssertion constraint, error1, error2;
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			error1 = sem_mutation.get_assertions().neg_value(expression);
			error2 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error1, error2 });
		}
	}
	
	@Override
	protected void generate_infections(AstMutation ast_mutation) throws Exception {
		switch(ast_mutation.get_mutation_operator()) {
		case insert_arith_neg:	this.insert_arith_neg(ast_mutation); break;
		case insert_bitws_rsv:	this.insert_bitws_rsv(ast_mutation); break;
		case insert_logic_not:	this.insert_logic_not(ast_mutation); break;
		case insert_abs:		this.insert_abs(ast_mutation); break;
		case insert_neg_abs:	this.insert_neg_abs(ast_mutation); break;
		default: throw new IllegalArgumentException("Invalid mutation operator");
		}
	}

}
