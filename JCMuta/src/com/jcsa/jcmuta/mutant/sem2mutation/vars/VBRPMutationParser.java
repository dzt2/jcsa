package com.jcsa.jcmuta.mutant.sem2mutation.vars;

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

public class VBRPMutationParser extends SemanticMutationParser {

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
	
	private void set_true(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_cir_result(ast_mutation);
		List<SemanticAssertion> state_errors = new ArrayList<SemanticAssertion>();
		Object constant = SemanticMutationUtil.get_constant(expression);
		
		if(constant != null) {
			if(constant instanceof Boolean) {
				if(!((Boolean) constant).booleanValue()) {
					state_errors.add(this.sem_mutation.get_assertions().set_value(expression, Boolean.TRUE));
				}
			}
			else if(constant instanceof Long) {
				if(((Long) constant).longValue() == 0) {
					state_errors.add(this.sem_mutation.get_assertions().set_value(expression, Boolean.TRUE));
				}
			}
			else if(constant instanceof Double) {
				if(((Double) constant).doubleValue() == 0) {
					state_errors.add(this.sem_mutation.get_assertions().set_value(expression, Boolean.TRUE));
				}
			}
			else throw new IllegalArgumentException("Invalid constant");
			
			if(!state_errors.isEmpty()) this.infect(state_errors);
		}
		else {
			CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
			
			SemanticAssertion constraint;
			if(CTypeAnalyzer.is_boolean(data_type)) {
				constraint = sem_mutation.get_assertions().equal_with(expression, Boolean.FALSE);
			}
			else if(CTypeAnalyzer.is_number(data_type)) {
				constraint = sem_mutation.get_assertions().equal_with(expression, Long.valueOf(0));
			}
			else if(CTypeAnalyzer.is_pointer(data_type)) {
				constraint = sem_mutation.get_assertions().equal_with(expression, Nullptr);
			}
			else throw new IllegalArgumentException("Invalid data type");
			
			state_errors.add(this.sem_mutation.get_assertions().set_value(expression, Boolean.TRUE));
			
			this.infect(constraint, state_errors);
		}
	}
	
	private void set_false(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_cir_result(ast_mutation);
		List<SemanticAssertion> state_errors = new ArrayList<SemanticAssertion>();
		Object constant = SemanticMutationUtil.get_constant(expression);
		
		if(constant != null) {
			if(constant instanceof Boolean) {
				if(((Boolean) constant).booleanValue()) {
					state_errors.add(this.sem_mutation.get_assertions().set_value(expression, Boolean.FALSE));
				}
			}
			else if(constant instanceof Long) {
				if(((Long) constant).longValue() != 0) {
					state_errors.add(this.sem_mutation.get_assertions().set_value(expression, Boolean.FALSE));
				}
			}
			else if(constant instanceof Double) {
				if(((Double) constant).doubleValue() != 0) {
					state_errors.add(this.sem_mutation.get_assertions().set_value(expression, Boolean.FALSE));
				}
			}
			else throw new IllegalArgumentException("Invalid constant: " + constant);
			
			this.infect(state_errors);
		}
		else {
			CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
			
			SemanticAssertion constraint;
			if(CTypeAnalyzer.is_boolean(data_type)) {
				constraint = sem_mutation.get_assertions().equal_with(expression, Boolean.TRUE);
			}
			else if(CTypeAnalyzer.is_number(data_type)) {
				constraint = sem_mutation.get_assertions().not_equals(expression, Long.valueOf(0));
			}
			else if(CTypeAnalyzer.is_pointer(data_type)) {
				constraint = sem_mutation.get_assertions().not_equals(expression, Nullptr);
			}
			else throw new IllegalArgumentException("Invalid data type");
			
			state_errors.add(this.sem_mutation.get_assertions().set_value(expression, Boolean.FALSE));
			
			this.infect(constraint, state_errors);
		}
	}
	
	@Override
	protected void generate_infections(AstMutation ast_mutation) throws Exception {
		switch(ast_mutation.get_mutation_operator()) {
		case set_true:	this.set_true(ast_mutation); 	break;
		case set_false:	this.set_false(ast_mutation);	break;
		default: throw new IllegalArgumentException("Invalid mutation operator");
		}
	}
	
}
