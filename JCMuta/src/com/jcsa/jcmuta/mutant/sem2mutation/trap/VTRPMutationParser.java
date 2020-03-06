package com.jcsa.jcmuta.mutant.sem2mutation.trap;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.SemanticMutationUtil;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertions;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class VTRPMutationParser extends SemanticMutationParser {

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
	
	@Override
	protected CirStatement get_statement(AstMutation ast_mutation) throws Exception {
		/*
		CirExpression expression = this.get_result(this.get_location(ast_mutation));
		if(expression != null) return expression.statement_of();
		else return null;
		*/
		return this.get_beg_statement(ast_mutation.get_location());
	}
	
	/**
	 * get the infection constraint
	 * @param ast_mutation
	 * @param expression
	 * @param assertions
	 * @return null if the constraint is impossible
	 * @throws Exception
	 */
	private SemanticAssertion get_constraint(AstMutation ast_mutation, 
			CirExpression expression, SemanticAssertions assertions) throws Exception {
		CType data_type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		if(CTypeAnalyzer.is_boolean(data_type)) {
			switch(ast_mutation.get_mutation_operator()) {
			case trap_on_pos:	return assertions.equal_with(expression, Boolean.TRUE);
			case trap_on_zro: 	return assertions.equal_with(expression, Boolean.FALSE);
			default: throw new IllegalArgumentException("Invalid operator");
			}
		}
		else if(CTypeAnalyzer.is_number(data_type)) {
			if(CTypeAnalyzer.is_unsigned(data_type)) {
				switch(ast_mutation.get_mutation_operator()) {
				case trap_on_pos:	return assertions.greater_tn(expression, Long.valueOf(0));
				case trap_on_zro:	return assertions.equal_with(expression, Long.valueOf(0));
				case trap_on_neg:	return null;
				default: throw new IllegalArgumentException("Invalid operator");
				}
			}
			else {
				switch(ast_mutation.get_mutation_operator()) {
				case trap_on_pos:	return assertions.greater_tn(expression, Long.valueOf(0));
				case trap_on_zro:	return assertions.equal_with(expression, Long.valueOf(0));
				case trap_on_neg:	return assertions.smaller_tn(expression, Long.valueOf(0));
				default: throw new IllegalArgumentException("Invalid operator");
				}
			}
		}
		else if(CTypeAnalyzer.is_pointer(data_type)) {
			switch(ast_mutation.get_mutation_operator()) {
			case trap_on_pos:	return assertions.greater_tn(expression, Nullptr);
			case trap_on_zro:	return assertions.equal_with(expression, Nullptr);
			case trap_on_neg:	return null;
			default: throw new IllegalArgumentException("Invalid operator");
			}
		}
		else throw new IllegalArgumentException("Invalid: " + data_type);
	}
	
	@Override
	protected void generate_infections(AstMutation ast_mutation) throws Exception {
		CirExpression expression = this.get_result(this.get_location(ast_mutation));
		Object constant = SemanticMutationUtil.get_constant(expression);
		if(constant != null) {
			switch(ast_mutation.get_mutation_operator()) {
			case trap_on_pos:
			{
				if(constant instanceof Boolean) {
					if(((Boolean) constant).booleanValue()) {
						this.infect(this.sem_mutation.get_assertions().trapping());
					}
				}
				else if(constant instanceof Long) {
					if(((Long) constant).longValue() > 0) {
						this.infect(this.sem_mutation.get_assertions().trapping());
					}
				}
				else if(constant instanceof Double) {
					if(((Double) constant).doubleValue() > 0) {
						this.infect(this.sem_mutation.get_assertions().trapping());
					}
				}
				else {
					throw new IllegalArgumentException("Invalid constant: " + constant);
				}
			}
			break;
			case trap_on_zro:
			{
				if(constant instanceof Boolean) {
					if(!((Boolean) constant).booleanValue()) {
						this.infect(this.sem_mutation.get_assertions().trapping());
					}
				}
				else if(constant instanceof Long) {
					if(((Long) constant).longValue() == 0) {
						this.infect(this.sem_mutation.get_assertions().trapping());
					}
				}
				else if(constant instanceof Double) {
					if(((Double) constant).doubleValue() == 0) {
						this.infect(this.sem_mutation.get_assertions().trapping());
					}
				}
				else {
					throw new IllegalArgumentException("Invalid constant: " + constant);
				}
			}
			break;
			case trap_on_neg:
			{
				if(constant instanceof Boolean) {
					
				}
				else if(constant instanceof Long) {
					if(((Long) constant).longValue() < 0) {
						this.infect(this.sem_mutation.get_assertions().trapping());
					}
				}
				else if(constant instanceof Double) {
					if(((Double) constant).doubleValue() < 0) {
						this.infect(this.sem_mutation.get_assertions().trapping());
					}
				}
				else {
					throw new IllegalArgumentException("Invalid constant: " + constant);
				}
			}
			break;
			default: throw new IllegalArgumentException("Invalid mutation operator");
			}
			
		}
		else {
			SemanticAssertion constraint = this.get_constraint(
					ast_mutation, expression, sem_mutation.get_assertions());
			SemanticAssertion state_error = sem_mutation.get_assertions().trapping();
			this.infect(constraint, state_error);
		}
		
	}

}
