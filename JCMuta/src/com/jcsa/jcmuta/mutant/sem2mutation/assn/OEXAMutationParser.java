package com.jcsa.jcmuta.mutant.sem2mutation.assn;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;

public class OEXAMutationParser extends SemanticMutationParser {
	
	/**
	 * get the assignment expression where the mutation is injected
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private AstBinaryExpression get_location(AstMutation ast_mutation) throws Exception {
		return (AstBinaryExpression) CTypeAnalyzer.
				get_expression_of((AstExpression) ast_mutation.get_location());
	}
	
	/**
	 * get the assignment statement representing the expression in assignment
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private CirAssignStatement get_assignment(AstMutation ast_mutation) throws Exception {
		return (CirAssignStatement) this.get_cir_node(this.get_location(ast_mutation), CirAssignStatement.class);
	}
	
	@Override
	protected CirStatement get_statement(AstMutation ast_mutation) throws Exception {
		return this.get_assignment(ast_mutation);
	}
	
	@Override
	protected void generate_infections(AstMutation ast_mutation) throws Exception {
		CirAssignStatement statement = this.get_assignment(ast_mutation);
		CirExpression expression = statement.get_rvalue();
		CirExpression loperand = statement.get_lvalue();
		CirExpression roperand = statement.get_rvalue();
		
		switch(ast_mutation.get_mutation_operator()) {
		case assign_to_arith_add_assign: this.assign_to_arith_add_assign(expression, loperand, roperand); break;
		case assign_to_arith_sub_assign: this.assign_to_arith_sub_assign(expression, loperand, roperand); break;
		case assign_to_arith_mul_assign: this.assign_to_arith_mul_assign(expression, loperand, roperand); break;
		case assign_to_arith_div_assign: this.assign_to_arith_div_assign(expression, loperand, roperand); break;
		case assign_to_arith_mod_assign: this.assign_to_arith_mod_assign(expression, loperand, roperand); break;
		
		case assign_to_bitws_and_assign: this.assign_to_bitws_and_assign(expression, loperand, roperand); break;
		case assign_to_bitws_ior_assign: this.assign_to_bitws_ior_assign(expression, loperand, roperand); break;
		case assign_to_bitws_xor_assign: this.assign_to_bitws_xor_assign(expression, loperand, roperand); break;
		case assign_to_bitws_lsh_assign: this.assign_to_bitws_lsh_assign(expression, loperand, roperand); break;
		case assign_to_bitws_rsh_assign: this.assign_to_bitws_rsh_assign(expression, loperand, roperand); break;
		
		default: throw new IllegalArgumentException("Invalid mutation operator.");
		}
		
	}
	
	/* implementation */
	private void assign_to_arith_add_assign(CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SemanticAssertion constraint;
		constraint = sem_mutation.get_assertions().not_equals(loperand, 0);
		this.infect(constraint, sem_mutation.get_assertions().mut_value(expression));
	}
	private void assign_to_arith_sub_assign(CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		this.infect(sem_mutation.get_assertions().mut_value(expression));
	}
	private void assign_to_arith_mul_assign(CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		this.infect(
				sem_mutation.get_assertions().not_equals(roperand, 0),
				sem_mutation.get_assertions().mut_value(expression));
	}
	private void assign_to_arith_div_assign(CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		this.infect(
				sem_mutation.get_assertions().not_equals(roperand, 0),
				sem_mutation.get_assertions().mut_value(expression));
		
		this.infect(
				sem_mutation.get_assertions().equal_with(roperand, 0),
				sem_mutation.get_assertions().trapping());
	}
	private void assign_to_arith_mod_assign(CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		this.infect(
				sem_mutation.get_assertions().not_equals(roperand, 0),
				sem_mutation.get_assertions().mut_value(expression));
		
		this.infect(
				sem_mutation.get_assertions().is_multiply(loperand, roperand),
				sem_mutation.get_assertions().set_value(expression, 0));
		
		this.infect(
				sem_mutation.get_assertions().equal_with(roperand, 0),
				sem_mutation.get_assertions().trapping());
	}
	private void assign_to_bitws_and_assign(CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		this.infect(
				sem_mutation.get_assertions().not_equals(roperand, 0),
				sem_mutation.get_assertions().mut_value(expression));
	}
	private void assign_to_bitws_ior_assign(CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SemanticAssertion constraint;
		constraint = sem_mutation.get_assertions().not_equals(loperand, 0);
		this.infect(constraint, sem_mutation.get_assertions().mut_value(expression));
	}
	private void assign_to_bitws_xor_assign(CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SemanticAssertion constraint;
		constraint = sem_mutation.get_assertions().not_equals(loperand, 0);
		this.infect(constraint, sem_mutation.get_assertions().mut_value(expression));
	}
	private void assign_to_bitws_lsh_assign(CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SemanticAssertion constraint;
		constraint = sem_mutation.get_assertions().not_equals(loperand, 0);
		this.infect(constraint, sem_mutation.get_assertions().mut_value(expression));
		
		constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
		this.infect(constraint, sem_mutation.get_assertions().set_value(expression, 0));
	}
	private void assign_to_bitws_rsh_assign(CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SemanticAssertion constraint;
		constraint = sem_mutation.get_assertions().not_equals(loperand, 0);
		this.infect(constraint, sem_mutation.get_assertions().mut_value(expression));
		
		constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
		this.infect(constraint, sem_mutation.get_assertions().set_value(expression, 0));
	}
	
}
