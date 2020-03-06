package com.jcsa.jcmuta.mutant.sem2mutation.trap;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.SemanticMutationUtil;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class BTRPMutationParser extends SemanticMutationParser {
	
	/**
	 * get the location where the mutation is injected
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private AstExpression get_location(AstMutation ast_mutation) throws Exception {
		return CTypeAnalyzer.get_expression_of((AstExpression) ast_mutation.get_location());
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
		CirExpression expression = this.get_cir_result(ast_mutation);
		if(expression != null) return expression.statement_of();
		else return null;
	}
	
	private void trap_on_true(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_cir_result(ast_mutation);
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		SemanticAssertion constraint, state_error;
		Object constant = SemanticMutationUtil.get_constant(expression);
		
		if(constant != null) {
			if(constant instanceof Boolean) {
				if(!((Boolean) constant).booleanValue()) return;	/* equivalent */
			}
			else if(constant instanceof Long) {
				if(((Long) constant).longValue() == 0) return;		/* equivalent */
			}
			else if(constant instanceof Double) {
				if(((Double) constant).doubleValue() == 0) return;	/* equivalent */
			}
			else throw new IllegalArgumentException("Invalid constant: " + constant);
			state_error = this.sem_mutation.get_assertions().trapping();
			this.infect(state_error);
		}
		else {
			if(CTypeAnalyzer.is_boolean(data_type)) {
				constraint = this.sem_mutation.get_assertions().equal_with(expression, Boolean.TRUE);
			}
			else if(CTypeAnalyzer.is_number(data_type)) {
				constraint = this.sem_mutation.get_assertions().not_equals(expression, Long.valueOf(0));
			}
			else if(CTypeAnalyzer.is_pointer(data_type)) {
				constraint = this.sem_mutation.get_assertions().not_equals(expression, Nullptr);
			}
			else {
				throw new IllegalArgumentException("Invalid data_type");
			}
			state_error = this.sem_mutation.get_assertions().trapping();
			this.infect(constraint, state_error);
		}
	}
	
	private void trap_on_false(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_cir_result(ast_mutation);
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		SemanticAssertion constraint, state_error;
		Object constant = SemanticMutationUtil.get_constant(expression);
		
		if(constant != null) {
			if(constant instanceof Boolean) {
				if(((Boolean) constant).booleanValue()) return;	/* equivalent */
			}
			else if(constant instanceof Long) {
				if(((Long) constant).longValue() != 0) return;		/* equivalent */
			}
			else if(constant instanceof Double) {
				if(((Double) constant).doubleValue() != 0) return;	/* equivalent */
			}
			else throw new IllegalArgumentException("Invalid constant: " + constant);
			state_error = this.sem_mutation.get_assertions().trapping();
			this.infect(state_error);
		}
		else {
			if(CTypeAnalyzer.is_boolean(data_type)) {
				constraint = this.sem_mutation.get_assertions().equal_with(expression, Boolean.FALSE);
			}
			else if(CTypeAnalyzer.is_number(data_type)) {
				constraint = this.sem_mutation.get_assertions().equal_with(expression, Long.valueOf(0));
			}
			else if(CTypeAnalyzer.is_pointer(data_type)) {
				constraint = this.sem_mutation.get_assertions().equal_with(expression, Nullptr);
			}
			else {
				throw new IllegalArgumentException("Invalid data_type");
			}
			state_error = this.sem_mutation.get_assertions().trapping();
			this.infect(constraint, state_error);
		}
	}
	
	@Override
	protected void generate_infections(AstMutation ast_mutation) throws Exception {
		switch(ast_mutation.get_mutation_operator()) {
		case trap_on_true: 		this.trap_on_true(ast_mutation); break;
		case trap_on_false: 	this.trap_on_false(ast_mutation); break;
		default: throw new IllegalArgumentException("Invalid mutation operator.");
		}
	}
	
}
