package com.jcsa.jcmuta.mutant.sem2mutation.assn;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.SemanticMutationUtil;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticMutationParser;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OBXAMutationParser extends SemanticMutationParser {
	
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
	
	/**
	 * get the expression being mutated
	 * @param ast_mutation
	 * @return
	 * @throws Exception
	 */
	private CirComputeExpression get_expression(AstMutation ast_mutation) throws Exception {
		return (CirComputeExpression) this.get_assignment(ast_mutation).get_rvalue();
	}

	@Override
	protected void generate_infections(AstMutation ast_mutation) throws Exception {
		CirComputeExpression expression = this.get_expression(ast_mutation);
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		Object lvalue = SemanticMutationUtil.get_constant(loperand);
		Object rvalue = SemanticMutationUtil.get_constant(roperand);
		
		switch(ast_mutation.get_mutation_operator()) {
		case bitws_and_assign_to_arith_add_assign:	this.bitws_and_to_arith_add(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_and_assign_to_arith_sub_assign:	this.bitws_and_to_arith_sub(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_and_assign_to_arith_mul_assign:	this.bitws_and_to_arith_mul(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_and_assign_to_arith_div_assign:	this.bitws_and_to_arith_div(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_and_assign_to_arith_mod_assign:	this.bitws_and_to_arith_mod(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_and_assign_to_bitws_ior_assign:	this.bitws_and_to_bitws_ior(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_and_assign_to_bitws_xor_assign:	this.bitws_and_to_bitws_xor(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_and_assign_to_bitws_lsh_assign:	this.bitws_and_to_bitws_lsh(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_and_assign_to_bitws_rsh_assign:	this.bitws_and_to_bitws_rsh(expression, loperand, roperand, lvalue, rvalue); break;
		
		case bitws_ior_assign_to_arith_add_assign:	this.bitws_ior_to_arith_add(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_assign_to_arith_sub_assign:	this.bitws_ior_to_arith_sub(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_assign_to_arith_mul_assign:	this.bitws_ior_to_arith_mul(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_assign_to_arith_div_assign:	this.bitws_ior_to_arith_div(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_assign_to_arith_mod_assign:	this.bitws_ior_to_arith_mod(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_assign_to_bitws_and_assign:	this.bitws_ior_to_bitws_and(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_assign_to_bitws_xor_assign:	this.bitws_ior_to_bitws_xor(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_assign_to_bitws_lsh_assign:	this.bitws_ior_to_bitws_lsh(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_assign_to_bitws_rsh_assign:	this.bitws_ior_to_bitws_rsh(expression, loperand, roperand, lvalue, rvalue); break;
		
		case bitws_xor_assign_to_arith_add_assign:	this.bitws_xor_to_arith_add(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_xor_assign_to_arith_sub_assign:	this.bitws_xor_to_arith_sub(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_xor_assign_to_arith_mul_assign:	this.bitws_xor_to_arith_mul(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_xor_assign_to_arith_div_assign:	this.bitws_xor_to_arith_div(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_xor_assign_to_arith_mod_assign:	this.bitws_xor_to_arith_mod(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_xor_assign_to_bitws_and_assign:	this.bitws_xor_to_bitws_and(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_xor_assign_to_bitws_ior_assign:	this.bitws_xor_to_bitws_ior(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_xor_assign_to_bitws_lsh_assign:	this.bitws_xor_to_bitws_lsh(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_xor_assign_to_bitws_rsh_assign:	this.bitws_xor_to_bitws_rsh(expression, loperand, roperand, lvalue, rvalue); break;
		
		case bitws_lsh_assign_to_arith_add_assign:	this.bitws_lsh_to_arith_add(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_lsh_assign_to_arith_sub_assign:	this.bitws_lsh_to_arith_sub(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_lsh_assign_to_arith_mul_assign:	this.bitws_lsh_to_arith_mul(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_lsh_assign_to_arith_div_assign:	this.bitws_lsh_to_arith_div(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_lsh_assign_to_arith_mod_assign:	this.bitws_lsh_to_arith_mod(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_lsh_assign_to_bitws_and_assign:	this.bitws_lsh_to_bitws_and(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_lsh_assign_to_bitws_ior_assign:	this.bitws_lsh_to_bitws_ior(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_lsh_assign_to_bitws_xor_assign:	this.bitws_lsh_to_bitws_xor(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_lsh_assign_to_bitws_rsh_assign:	this.bitws_lsh_to_bitws_rsh(expression, loperand, roperand, lvalue, rvalue); break;
		
		case bitws_rsh_assign_to_arith_add_assign:	this.bitws_rsh_to_arith_add(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_rsh_assign_to_arith_sub_assign:	this.bitws_rsh_to_arith_sub(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_rsh_assign_to_arith_mul_assign:	this.bitws_rsh_to_arith_mul(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_rsh_assign_to_arith_div_assign:	this.bitws_rsh_to_arith_div(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_rsh_assign_to_arith_mod_assign:	this.bitws_rsh_to_arith_mod(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_rsh_assign_to_bitws_and_assign:	this.bitws_rsh_to_bitws_and(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_rsh_assign_to_bitws_ior_assign:	this.bitws_rsh_to_bitws_ior(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_rsh_assign_to_bitws_xor_assign:	this.bitws_rsh_to_bitws_xor(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_rsh_assign_to_bitws_lsh_assign:	this.bitws_rsh_to_bitws_lsh(expression, loperand, roperand, lvalue, rvalue); break;
		
		case bitws_and_assign_to_assign:	this.bitws_and_to_assign(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_assign_to_assign:	this.bitws_ior_to_assign(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_xor_assign_to_assign:	this.bitws_xor_to_assign(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_lsh_assign_to_assign:	this.bitws_lsh_to_assign(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_rsh_assign_to_assign:	this.bitws_rsh_to_assign(expression, loperand, roperand, lvalue, rvalue); break;
		
		default: throw new IllegalArgumentException("Invalid mutation operator");
		}
	}
	
	/* bitws and */
	private void bitws_and_to_arith_add(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_and, loperand, roperand, COperator.arith_add);
		}
		else {
			SemanticAssertion cons1, cons2, error2, error3;
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			cons1 = sem_mutation.get_assertions().greater_tn(expression,0);
			this.infect(cons1, new SemanticAssertion[] { error3 });
			
			cons2 = sem_mutation.get_assertions().smaller_tn(expression,0);
			this.infect(cons2, new SemanticAssertion[] { error2 });
		}
	}
	private void bitws_and_to_arith_sub(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_and, loperand, roperand, COperator.arith_sub);
		}
		else {
			SemanticAssertion constraint, state_error2;
			state_error2 = sem_mutation.get_assertions().set_value(expression, 0);
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(sem_mutation.get_assertions().mut_value(expression));
			this.infect(constraint, new SemanticAssertion[] { state_error2 });
		}
	}
	private void bitws_and_to_arith_mul(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_and, loperand, roperand, COperator.arith_mul);
		}
		else {
			SemanticAssertion cons1, cons2, error1, error2, error3;
			cons1 = sem_mutation.get_assertions().not_equals(loperand, 0);
			cons2 = sem_mutation.get_assertions().not_equals(roperand, 0);
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, error1);
			
			this.infect(
					sem_mutation.get_assertions().greater_tn(expression, 0), 
					new SemanticAssertion[] { error3 });
			this.infect(
					sem_mutation.get_assertions().smaller_tn(expression, 0), 
					new SemanticAssertion[] { error2 });
		}
	}
	private void bitws_and_to_arith_div(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_and, loperand, roperand, COperator.arith_div);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, sem_mutation.get_assertions().trapping());
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
		}
	}
	private void bitws_and_to_arith_mod(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_and, loperand, roperand, COperator.arith_mod);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
		}
	}
	private void bitws_and_to_bitws_ior(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_and, loperand, roperand, COperator.bit_or);
		}
		else {
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().not_equals(loperand, roperand);
			this.infect(constraint, sem_mutation.get_assertions().mut_value(expression));
		}
	}
	private void bitws_and_to_bitws_xor(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_and, loperand, roperand, COperator.bit_xor);
		}
		else {
			SemanticAssertion constraint, error2;
			error2 = sem_mutation.get_assertions().inc_value(expression);
			
			constraint = sem_mutation.get_assertions().not_equals(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
		}
	}
	private void bitws_and_to_bitws_lsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_and, loperand, roperand, COperator.left_shift);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void bitws_and_to_bitws_rsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_and, loperand, roperand, COperator.righ_shift);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	
	/* bitws ior */
	private void bitws_ior_to_arith_add(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_or, loperand, roperand, COperator.arith_add);
		}
		else {
			SemanticAssertion constraint, error1, error2, error3;
			
			constraint = sem_mutation.get_assertions().bit_intersect(loperand, roperand);
			error1 = sem_mutation.get_assertions().mut_value(expression);
			this.infect(constraint, error1);
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			error2 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void bitws_ior_to_arith_sub(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_or, loperand, roperand, COperator.arith_sub);
		}
		else {
			SemanticAssertion constraint, error1, error2, error3;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
			this.infect(constraint, error1);
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
		}
	}
	private void bitws_ior_to_arith_mul(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_or, loperand, roperand, COperator.arith_mul);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void bitws_ior_to_arith_div(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_or, loperand, roperand, COperator.arith_div);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, sem_mutation.get_assertions().trapping());
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void bitws_ior_to_arith_mod(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_or, loperand, roperand, COperator.arith_mod);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, sem_mutation.get_assertions().trapping());
			
			constraint = sem_mutation.get_assertions().is_multiply(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void bitws_ior_to_bitws_and(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_or, loperand, roperand, COperator.bit_and);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().not_equals(loperand, roperand);
			this.infect(constraint, sem_mutation.get_assertions().mut_value(expression));
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] {error2 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void bitws_ior_to_bitws_xor(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_or, loperand, roperand, COperator.bit_xor);
		}
		else {
			SemanticAssertion constraint, error1, error2, error3, error4;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			SemanticAssertion cons1 = sem_mutation.get_assertions().not_equals(loperand, 0);
			SemanticAssertion cons2 = sem_mutation.get_assertions().not_equals(roperand, 0);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, error1);
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void bitws_ior_to_bitws_lsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_or, loperand, roperand, COperator.left_shift);
		}
		else {
			SemanticAssertion constraint, error1, error2, error3, error4;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			error4 = sem_mutation.get_assertions().set_value(expression, 0);
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(roperand, 0);
			this.infect(constraint, error1);
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void bitws_ior_to_bitws_rsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_or, loperand, roperand, COperator.righ_shift);
		}
		else {
			SemanticAssertion constraint, error1, error2, error3, error4;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			error4 = sem_mutation.get_assertions().set_value(expression, 0);
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(roperand, 0);
			this.infect(constraint, error1);
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
		}
	}
	
	/* bitws xor */
	private void bitws_xor_to_arith_add(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_xor, loperand, roperand, COperator.arith_add);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void bitws_xor_to_arith_sub(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_xor, loperand, roperand, COperator.arith_sub);
		}
		else {
			SemanticAssertion constraint, error2;
			constraint = sem_mutation.get_assertions().not_subsuming(loperand, roperand);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
		}
	}
	private void bitws_xor_to_arith_mul(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_xor, loperand, roperand, COperator.arith_mul);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void bitws_xor_to_arith_div(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_xor, loperand, roperand, COperator.arith_div);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, sem_mutation.get_assertions().trapping());
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void bitws_xor_to_arith_mod(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_xor, loperand, roperand, COperator.arith_mod);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, sem_mutation.get_assertions().trapping());
			
			constraint = sem_mutation.get_assertions().is_multiply(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().not_in_range(loperand, "[c, 2c)");
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void bitws_xor_to_bitws_and(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_xor, loperand, roperand, COperator.bit_and);
		}
		else {
			SemanticAssertion constraint, error1, error2;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().not_equals(loperand, 0);
			this.infect(constraint, error1);
			
			constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
			this.infect(constraint, error1);
		}
	}
	private void bitws_xor_to_bitws_ior(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_xor, loperand, roperand, COperator.bit_or);
		}
		else {
			SemanticAssertion constraint, error1, error2, error3;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().bit_intersect(loperand, roperand);
			this.infect(constraint, error1);
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void bitws_xor_to_bitws_lsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_xor, loperand, roperand, COperator.left_shift);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void bitws_xor_to_bitws_rsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_xor, loperand, roperand, COperator.righ_shift);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	
	/* bitws lsh */
	private void bitws_lsh_to_arith_add(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					left_shift, loperand, roperand, COperator.arith_add);
		}
		else {
			SemanticAssertion cons1, cons2, error2, error3;
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, new SemanticAssertion[] { error3 });
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, new SemanticAssertion[] { error2 });
			
			cons1 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(cons1, new SemanticAssertion[] { error2 });
			
			cons2 = sem_mutation.get_assertions().not_equals(roperand, 0);
			this.infect(cons2, sem_mutation.get_assertions().mut_value(expression));
		}  
	}
	private void bitws_lsh_to_arith_sub(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					left_shift, loperand, roperand, COperator.arith_sub);
		}
		else {
			SemanticAssertion cons1, cons2, error1, error2, error3;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, new SemanticAssertion[] { error3 });
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, new SemanticAssertion[] { error2 });
			
			cons1 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(cons1, new SemanticAssertion[] { error3 });
			
			cons2 = sem_mutation.get_assertions().not_equals(roperand, 0);
			this.infect(cons2, error1);
		}
	}
	private void bitws_lsh_to_arith_mul(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					left_shift, loperand, roperand, COperator.arith_mul);
		}
		else {
			SemanticAssertion cons1, cons2, error1, error2, error3, error4;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			cons1 = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(cons1, new SemanticAssertion[] { error2 });
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] {cons1, cons2}, new SemanticAssertion[] {error4});
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] {cons1, cons2}, new SemanticAssertion[] {error3});
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] {cons1, cons2}, new SemanticAssertion[] {error3});
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] {cons1, cons2}, new SemanticAssertion[] {error4});
			
			cons1 = sem_mutation.get_assertions().not_equals(loperand, 0);
			this.infect(cons1, error1);
		}
	}
	private void bitws_lsh_to_arith_div(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					left_shift, loperand, roperand, COperator.arith_div);
		}
		else {
			SemanticAssertion cons1, cons2, error1, error3, error4;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			cons1 = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(cons1, sem_mutation.get_assertions().trapping());
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
					new SemanticAssertion[] { error4 });
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
					new SemanticAssertion[] { error3 });
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
					new SemanticAssertion[] { error3 });
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
					new SemanticAssertion[] { error4 });
			
			cons1 = sem_mutation.get_assertions().not_equals(loperand, 0);
			this.infect(cons1, error1);
		}
	}
	private void bitws_lsh_to_arith_mod(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					left_shift, loperand, roperand, COperator.arith_mod);
		}
		else {
			SemanticAssertion cons1, cons2, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			cons1 = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(cons1, sem_mutation.get_assertions().trapping());
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
					new SemanticAssertion[] { error4 });
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			this.infect(cons1, new SemanticAssertion[] { error3 });
			
			cons1 = sem_mutation.get_assertions().is_multiply(loperand, roperand);
			this.infect(cons1, new SemanticAssertion[] { error2 });
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
						new SemanticAssertion[] { error4 });
		}
	}
	private void bitws_lsh_to_bitws_and(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					left_shift, loperand, roperand, COperator.bit_and);
		}
		else {
			SemanticAssertion cons1, cons2, error1, error2, error3, error4;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			cons1 = sem_mutation.get_assertions().not_equals(loperand, 0);
			this.infect(cons1, error1);
			
			cons1 = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(cons1, new SemanticAssertion[] { error2 });
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
					new SemanticAssertion[] { error4 });
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
					new SemanticAssertion[] { error3 });
			
			cons1 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(cons1, new SemanticAssertion[] { error3 });
		}
	}
	private void bitws_lsh_to_bitws_ior(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					left_shift, loperand, roperand, COperator.bit_or);
		}
		else {
			SemanticAssertion cons1, cons2, error1, error2, error3;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			cons1 = sem_mutation.get_assertions().greater_tn(roperand, 0);
			this.infect(cons1, error1);
			
			cons1 = sem_mutation.get_assertions().equal_with(loperand, 0);
			cons2 = sem_mutation.get_assertions().greater_tn(roperand, 0);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, new SemanticAssertion[] { error2 });
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, new SemanticAssertion[] { error3 });
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, new SemanticAssertion[] { error2 });
			
			cons2 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(cons2, new SemanticAssertion[] { error2 });
		}
	}
	private void bitws_lsh_to_bitws_xor(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					left_shift, loperand, roperand, COperator.bit_xor);
		}
		else {
			SemanticAssertion cons1, cons2, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			cons1 = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(cons1, new SemanticAssertion[] { error2 });
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
						new SemanticAssertion[] { error4 });
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
						new SemanticAssertion[] { error3 });
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
					new SemanticAssertion[] { error3 });
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
					new SemanticAssertion[] { error4 });
		}
	}
	private void bitws_lsh_to_bitws_rsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					left_shift, loperand, roperand, COperator.righ_shift);
		}
		else {
			SemanticAssertion cons1, cons2, error1, error2, error3;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			cons1 = sem_mutation.get_assertions().not_equals(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, error1);
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, new SemanticAssertion[] { error3 });
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, new SemanticAssertion[] { error2 });
		}
	}
	
	/* bitws rsh */
	private void bitws_rsh_to_arith_add(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					righ_shift, loperand, roperand, COperator.arith_add);
		}
		else {
			SemanticAssertion cons1, cons2, error2, error3;
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, new SemanticAssertion[] { error2 });
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, new SemanticAssertion[] { error3 });
			
			cons1 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(cons1, new SemanticAssertion[] { error2 });
			
			cons2 = sem_mutation.get_assertions().not_equals(roperand, 0);
			this.infect(cons2, sem_mutation.get_assertions().mut_value(expression));
		}  
	}
	private void bitws_rsh_to_arith_sub(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					righ_shift, loperand, roperand, COperator.arith_sub);
		}
		else {
			SemanticAssertion cons1, cons2, error1, error2, error3;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, new SemanticAssertion[] { error2 });
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, new SemanticAssertion[] { error3 });
			
			cons1 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(cons1, new SemanticAssertion[] { error3 });
			
			cons2 = sem_mutation.get_assertions().not_equals(roperand, 0);
			this.infect(cons2, error1);
		}
	}
	private void bitws_rsh_to_arith_mul(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					righ_shift, loperand, roperand, COperator.arith_mul);
		}
		else {
			SemanticAssertion cons1, cons2, error1, error2, error3, error4;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			cons1 = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(cons1, new SemanticAssertion[] { error2 });
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] {cons1, cons2}, new SemanticAssertion[] {error3});
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] {cons1, cons2}, new SemanticAssertion[] {error4});
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] {cons1, cons2}, new SemanticAssertion[] {error4});
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] {cons1, cons2}, new SemanticAssertion[] {error3});
			
			cons1 = sem_mutation.get_assertions().not_equals(loperand, 0);
			this.infect(cons1, error1);
		}
	}
	private void bitws_rsh_to_arith_div(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					righ_shift, loperand, roperand, COperator.arith_div);
		}
		else {
			SemanticAssertion cons1, cons2, error1, error3, error4;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			cons1 = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(cons1, sem_mutation.get_assertions().trapping());
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
					new SemanticAssertion[] { error3 });
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
					new SemanticAssertion[] { error4 });
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
					new SemanticAssertion[] { error4 });
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
					new SemanticAssertion[] { error3 });
			
			cons1 = sem_mutation.get_assertions().not_equals(loperand, 0);
			this.infect(cons1, error1);
		}
	}
	private void bitws_rsh_to_arith_mod(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					righ_shift, loperand, roperand, COperator.arith_mod);
		}
		else {
			SemanticAssertion cons1, cons2, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			cons1 = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(cons1, sem_mutation.get_assertions().trapping());
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
					new SemanticAssertion[] { error3 });
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			this.infect(cons1, new SemanticAssertion[] { error4 });
			
			cons1 = sem_mutation.get_assertions().is_multiply(loperand, roperand);
			this.infect(cons1, new SemanticAssertion[] { error2 });
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
						new SemanticAssertion[] { error3 });
		}
	}
	private void bitws_rsh_to_bitws_and(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					righ_shift, loperand, roperand, COperator.bit_and);
		}
		else {
			SemanticAssertion cons1, cons2, error1, error2, error3, error4;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			cons1 = sem_mutation.get_assertions().not_equals(loperand, 0);
			this.infect(cons1, error1);
			
			cons1 = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(cons1, new SemanticAssertion[] { error2 });
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
					new SemanticAssertion[] { error3 });
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
					new SemanticAssertion[] { error4 });
			
			cons1 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(cons1, new SemanticAssertion[] { error4 });
		}
	}
	private void bitws_rsh_to_bitws_ior(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					righ_shift, loperand, roperand, COperator.bit_or);
		}
		else {
			SemanticAssertion cons1, cons2, error1, error2, error3;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			cons1 = sem_mutation.get_assertions().greater_tn(roperand, 0);
			this.infect(cons1, error1);
			
			cons1 = sem_mutation.get_assertions().equal_with(loperand, 0);
			cons2 = sem_mutation.get_assertions().greater_tn(roperand, 0);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, new SemanticAssertion[] { error2 });
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, new SemanticAssertion[] { error2 });
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, new SemanticAssertion[] { error3 });
			
			cons2 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(cons2, new SemanticAssertion[] { error2 });
		}
	}
	private void bitws_rsh_to_bitws_xor(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					righ_shift, loperand, roperand, COperator.bit_xor);
		}
		else {
			SemanticAssertion cons1, cons2, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			cons1 = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(cons1, new SemanticAssertion[] { error2 });
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
						new SemanticAssertion[] { error3 });
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
						new SemanticAssertion[] { error4 });
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
					new SemanticAssertion[] { error3 });
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, 
					new SemanticAssertion[] { error4 });
		}
	}
	private void bitws_rsh_to_bitws_lsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					righ_shift, loperand, roperand, COperator.left_shift);
		}
		else {
			SemanticAssertion cons1, cons2, error1, error2, error3;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			cons1 = sem_mutation.get_assertions().not_equals(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, error1);
			
			cons1 = sem_mutation.get_assertions().greater_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, new SemanticAssertion[] { error2 });
			
			cons1 = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			cons2 = sem_mutation.get_assertions().smaller_tn(roperand, max_shifting);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, new SemanticAssertion[] { error3 });
		}
	}
	
	/* assign */
	private void bitws_and_to_assign(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(rvalue != null) {
			if(rvalue instanceof Boolean) {
				this.infect(sem_mutation.get_assertions().set_value(expression, ((Boolean) rvalue).booleanValue()));
			}
			else if(rvalue instanceof Integer) {
				this.infect(sem_mutation.get_assertions().set_value(expression, ((Integer) rvalue).longValue()));
			}
			else if(rvalue instanceof Long) {
				this.infect(sem_mutation.get_assertions().set_value(expression, ((Long) rvalue).longValue()));
			}
			else if(rvalue instanceof Double) {
				this.infect(sem_mutation.get_assertions().set_value(expression, ((Double) rvalue).doubleValue()));
			}
			else {
				throw new IllegalArgumentException("Invalid rvalue");
			}
		}
		else {
			this.infect(
					sem_mutation.get_assertions().not_equals(loperand, roperand), 
					sem_mutation.get_assertions().mut_value(expression));
		}
	}
	private void bitws_ior_to_assign(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(rvalue != null) {
			if(rvalue instanceof Boolean) {
				this.infect(sem_mutation.get_assertions().set_value(expression, ((Boolean) rvalue).booleanValue()));
			}
			else if(rvalue instanceof Integer) {
				this.infect(sem_mutation.get_assertions().set_value(expression, ((Integer) rvalue).longValue()));
			}
			else if(rvalue instanceof Long) {
				this.infect(sem_mutation.get_assertions().set_value(expression, ((Long) rvalue).longValue()));
			}
			else if(rvalue instanceof Double) {
				this.infect(sem_mutation.get_assertions().set_value(expression, ((Double) rvalue).doubleValue()));
			}
			else {
				throw new IllegalArgumentException("Invalid rvalue");
			}
		}
		else {
			this.infect(
					sem_mutation.get_assertions().not_subsuming(loperand, roperand), 
					sem_mutation.get_assertions().mut_value(expression));
		}
	}
	private void bitws_xor_to_assign(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(rvalue != null) {
			if(rvalue instanceof Boolean) {
				this.infect(sem_mutation.get_assertions().set_value(expression, ((Boolean) rvalue).booleanValue()));
			}
			else if(rvalue instanceof Integer) {
				this.infect(sem_mutation.get_assertions().set_value(expression, ((Integer) rvalue).longValue()));
			}
			else if(rvalue instanceof Long) {
				this.infect(sem_mutation.get_assertions().set_value(expression, ((Long) rvalue).longValue()));
			}
			else if(rvalue instanceof Double) {
				this.infect(sem_mutation.get_assertions().set_value(expression, ((Double) rvalue).doubleValue()));
			}
			else {
				throw new IllegalArgumentException("Invalid rvalue");
			}
		}
		else {
			this.infect(
					sem_mutation.get_assertions().not_equals(loperand, 0), 
					sem_mutation.get_assertions().mut_value(expression));
		}
	}
	private void bitws_lsh_to_assign(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(rvalue != null) {
			if(rvalue instanceof Boolean) {
				this.infect(sem_mutation.get_assertions().set_value(expression, ((Boolean) rvalue).booleanValue()));
			}
			else if(rvalue instanceof Integer) {
				this.infect(sem_mutation.get_assertions().set_value(expression, ((Integer) rvalue).longValue()));
			}
			else if(rvalue instanceof Long) {
				this.infect(sem_mutation.get_assertions().set_value(expression, ((Long) rvalue).longValue()));
			}
			else if(rvalue instanceof Double) {
				this.infect(sem_mutation.get_assertions().set_value(expression, ((Double) rvalue).doubleValue()));
			}
			else {
				throw new IllegalArgumentException("Invalid rvalue");
			}
		}
		else {
			this.infect(
					sem_mutation.get_assertions().greater_tn(loperand, 0), 
					sem_mutation.get_assertions().dec_value(expression));
			
			this.infect(
					sem_mutation.get_assertions().smaller_tn(loperand, 0), 
					sem_mutation.get_assertions().inc_value(expression));
		}
	}
	private void bitws_rsh_to_assign(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(rvalue != null) {
			if(rvalue instanceof Boolean) {
				this.infect(sem_mutation.get_assertions().set_value(expression, ((Boolean) rvalue).booleanValue()));
			}
			else if(rvalue instanceof Integer) {
				this.infect(sem_mutation.get_assertions().set_value(expression, ((Integer) rvalue).longValue()));
			}
			else if(rvalue instanceof Long) {
				this.infect(sem_mutation.get_assertions().set_value(expression, ((Long) rvalue).longValue()));
			}
			else if(rvalue instanceof Double) {
				this.infect(sem_mutation.get_assertions().set_value(expression, ((Double) rvalue).doubleValue()));
			}
			else {
				throw new IllegalArgumentException("Invalid rvalue");
			}
		}
		else {
			this.infect(
					sem_mutation.get_assertions().greater_tn(loperand, 0), 
					sem_mutation.get_assertions().inc_value(expression));
			
			this.infect(
					sem_mutation.get_assertions().smaller_tn(loperand, 0), 
					sem_mutation.get_assertions().dec_value(expression));
		}
	}
	
}
