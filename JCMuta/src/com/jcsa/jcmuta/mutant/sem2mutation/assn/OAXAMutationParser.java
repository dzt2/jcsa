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

public class OAXAMutationParser extends SemanticMutationParser {
	
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
		case arith_add_assign_to_arith_sub_assign:	this.arith_add_to_arith_sub(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_add_assign_to_arith_mul_assign:	this.arith_add_to_arith_mul(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_add_assign_to_arith_div_assign:	this.arith_add_to_arith_div(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_add_assign_to_arith_mod_assign:	this.arith_add_to_arith_mod(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_add_assign_to_bitws_and_assign:	this.arith_add_to_bitws_and(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_add_assign_to_bitws_ior_assign:	this.arith_add_to_bitws_ior(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_add_assign_to_bitws_xor_assign:	this.arith_add_to_bitws_xor(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_add_assign_to_bitws_lsh_assign:	this.arith_add_to_bitws_lsh(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_add_assign_to_bitws_rsh_assign:	this.arith_add_to_bitws_rsh(expression, loperand, roperand, lvalue, rvalue); break;
		
		case arith_sub_assign_to_arith_add_assign:	this.arith_sub_to_arith_add(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_sub_assign_to_arith_mul_assign:	this.arith_sub_to_arith_mul(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_sub_assign_to_arith_div_assign:	this.arith_sub_to_arith_div(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_sub_assign_to_arith_mod_assign:	this.arith_sub_to_arith_mod(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_sub_assign_to_bitws_and_assign:	this.arith_sub_to_bitws_and(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_sub_assign_to_bitws_ior_assign:	this.arith_sub_to_bitws_ior(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_sub_assign_to_bitws_xor_assign:	this.arith_sub_to_bitws_xor(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_sub_assign_to_bitws_lsh_assign:	this.arith_sub_to_bitws_lsh(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_sub_assign_to_bitws_rsh_assign:	this.arith_sub_to_bitws_rsh(expression, loperand, roperand, lvalue, rvalue); break; 
		
		case arith_mul_assign_to_arith_add_assign:	this.arith_mul_to_arith_add(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_assign_to_arith_sub_assign:	this.arith_mul_to_arith_sub(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_assign_to_arith_div_assign:	this.arith_mul_to_arith_div(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_assign_to_arith_mod_assign:	this.arith_mul_to_arith_mod(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_assign_to_bitws_and_assign:	this.arith_mul_to_bitws_and(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_assign_to_bitws_ior_assign:	this.arith_mul_to_bitws_ior(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_assign_to_bitws_xor_assign:	this.arith_mul_to_bitws_xor(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_assign_to_bitws_lsh_assign:	this.arith_mul_to_bitws_lsh(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_assign_to_bitws_rsh_assign:	this.arith_mul_to_bitws_rsh(expression, loperand, roperand, lvalue, rvalue); break; 
		
		case arith_div_assign_to_arith_add_assign:	this.arith_div_to_arith_add(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_div_assign_to_arith_sub_assign:	this.arith_div_to_arith_sub(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_div_assign_to_arith_mul_assign:	this.arith_div_to_arith_mul(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_div_assign_to_arith_mod_assign:	this.arith_div_to_arith_mod(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_div_assign_to_bitws_and_assign:	this.arith_div_to_bitws_and(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_div_assign_to_bitws_ior_assign:	this.arith_div_to_bitws_ior(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_div_assign_to_bitws_xor_assign:	this.arith_div_to_bitws_xor(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_div_assign_to_bitws_lsh_assign:	this.arith_div_to_bitws_lsh(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_div_assign_to_bitws_rsh_assign:	this.arith_div_to_bitws_rsh(expression, loperand, roperand, lvalue, rvalue); break; 
		
		case arith_mod_assign_to_arith_add_assign:	this.arith_mod_to_arith_add(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_assign_to_arith_sub_assign:	this.arith_mod_to_arith_sub(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_assign_to_arith_mul_assign:	this.arith_mod_to_arith_mul(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_assign_to_arith_div_assign:	this.arith_mod_to_arith_div(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_assign_to_bitws_and_assign:	this.arith_mod_to_bitws_and(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_assign_to_bitws_ior_assign:	this.arith_mod_to_bitws_ior(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_assign_to_bitws_xor_assign:	this.arith_mod_to_bitws_xor(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_assign_to_bitws_lsh_assign:	this.arith_mod_to_bitws_lsh(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_assign_to_bitws_rsh_assign:	this.arith_mod_to_bitws_rsh(expression, loperand, roperand, lvalue, rvalue); break; 
		
		case arith_add_assign_to_assign:	this.arith_add_to_assign(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_sub_assign_to_assign:	this.arith_sub_to_assign(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_assign_to_assign:	this.arith_mul_to_assign(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_div_assign_to_assign:	this.arith_div_to_assign(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_assign_to_assign:	this.arith_mod_to_assign(expression, loperand, roperand, lvalue, rvalue); break;
		
		default: throw new IllegalArgumentException("Invalid mutation operator");
		}
	}
	
	/* arith add */
	/**
	 * (+, -)
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_add_to_arith_sub(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, 
			Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_add, loperand, roperand, COperator.arith_sub);
		}
		else if(rvalue != null) {
			this.arith_add_to_arith_sub_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error1, error2, error3;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error1 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void arith_add_to_arith_sub_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 0) { /** equivalent **/ }
			else {
				this.infect(sem_mutation.get_assertions().diff_value(expression, -2 * value));
			}
		}
		else if(rvalue instanceof Double) {
			double value = ((Double) rvalue).doubleValue();
			if(value == 0) { /** equivalent **/ }
			else {
				this.infect(sem_mutation.get_assertions().diff_value(expression, -2 * value));
			}
		}
		else throw new IllegalArgumentException("Invalid value");
	}
	/**
	 * (+, *)
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_add_to_arith_mul(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, 
			Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_add, loperand, roperand, COperator.arith_mul);
		}
		else if(lvalue != null) {
			this.arith_add_to_arith_mul_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_add_to_arith_mul_cv(expression, rvalue, loperand);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			this.infect(new SemanticAssertion[] { error3 });
		}
	}
	private void arith_add_to_arith_mul_cv(CirExpression expression, 
			Object lvalue, CirExpression roperand) throws Exception {
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) {
				this.infect(sem_mutation.get_assertions().set_value(expression, 0));
			}
			else if(value == 1) {
				this.infect(sem_mutation.get_assertions().diff_value(expression, -1));
			}
			else if(value > 1) {
				this.infect(sem_mutation.get_assertions().inc_value(expression));
			}
			else {
				this.infect(sem_mutation.get_assertions().dec_value(expression));
			}
		}
		else if(lvalue instanceof Double) {
			double value = ((Double) lvalue).doubleValue();
			if(value == 0) {
				this.infect(sem_mutation.get_assertions().set_value(expression, 0));
			}
			else if(value == 1) {
				this.infect(sem_mutation.get_assertions().diff_value(expression, -1));
			}
			else if(value > 1) {
				this.infect(sem_mutation.get_assertions().inc_value(expression));
			}
			else {
				this.infect(sem_mutation.get_assertions().dec_value(expression));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_add_to_arith_div(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, 
			Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_add, loperand, roperand, COperator.arith_div);
		}
		else if(lvalue != null) {
			this.arith_add_to_arith_div_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_add_to_arith_div_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint;
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, sem_mutation.get_assertions().trapping());
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, sem_mutation.get_assertions().set_value(expression, 0));
			
			this.infect(sem_mutation.get_assertions().dec_value(expression));
		}
	}
	private void arith_add_to_arith_div_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) {
				this.infect(sem_mutation.get_assertions().set_value(expression, 0));
			}
			else if(value > 0) {
				this.infect(sem_mutation.get_assertions().dec_value(expression));
			}
			else {
				this.infect(sem_mutation.get_assertions().inc_value(expression));
			}
		}
		else if(lvalue instanceof Double) {
			double value = ((Double) lvalue).doubleValue();
			if(value == 0) {
				this.infect(sem_mutation.get_assertions().set_value(expression, 0));
			}
			else if(value > 0) {
				this.infect(sem_mutation.get_assertions().dec_value(expression));
			}
			else {
				this.infect(sem_mutation.get_assertions().inc_value(expression));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private void arith_add_to_arith_div_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 0) {
				this.infect(sem_mutation.get_assertions().trapping());
			}
			else if(value > 1) {
				this.infect(sem_mutation.get_assertions().dec_value(expression));
			}
			else {
				this.infect(sem_mutation.get_assertions().inc_value(expression));
			}
		}
		else if(rvalue instanceof Double) {
			double value = ((Double) rvalue).doubleValue();
			if(value == 0) {
				this.infect(sem_mutation.get_assertions().trapping());
			}
			else if(value > 1) {
				this.infect(sem_mutation.get_assertions().dec_value(expression));
			}
			else {
				this.infect(sem_mutation.get_assertions().inc_value(expression));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_add_to_arith_mod(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, 
			Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_add, loperand, roperand, COperator.arith_mod);
		}
		else if(lvalue != null) {
			this.arith_add_to_arith_mod_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_add_to_arith_mod_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint;
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, sem_mutation.get_assertions().trapping());
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, sem_mutation.get_assertions().set_value(expression, 0));
			
			constraint = sem_mutation.get_assertions().not_in_range(loperand, "[-c, 0)");
			this.infect(constraint, sem_mutation.get_assertions().mut_value(expression));
		}
	}
	private void arith_add_to_arith_mod_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) {
				this.infect(sem_mutation.get_assertions().set_value(expression, 0));
			}
			else if(value == 1 || value == -1) {
				this.infect(sem_mutation.get_assertions().set_value(expression, 1));
			}
			else if(value > 1) {
				this.infect(sem_mutation.get_assertions().dec_value(expression));
			}
			else {
				this.infect(sem_mutation.get_assertions().inc_value(expression));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private void arith_add_to_arith_mod_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 0) {
				this.infect(sem_mutation.get_assertions().trapping());
			}
			else if(value == 1 || value == -1) {
				this.infect(sem_mutation.get_assertions().set_value(expression, 0));
			}
			else {
				this.infect(sem_mutation.get_assertions().mut_value(expression));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_add_to_bitws_and(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, 
			Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_add, loperand, roperand, COperator.bit_and);
		}
		else if(lvalue != null) {
			this.arith_add_to_bitws_and_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_add_to_bitws_and_cv(expression, rvalue, loperand);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			this.infect(new SemanticAssertion[] { error3 });
		}
	}
	private void arith_add_to_bitws_and_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion error2, error3;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) {
				this.infect(sem_mutation.get_assertions().set_value(expression, 0));
			}
			else if(this.count_bit_ones(value) <= min_bit_ones) {
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				error3 = sem_mutation.get_assertions().set_value(expression, value);
				this.infect(new SemanticAssertion[] { error2, error3 });
			}
			else {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value: " + lvalue);
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_add_to_bitws_ior(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, 
			Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_add, loperand, roperand, COperator.bit_or);
		}
		else if(lvalue != null) {
			this.arith_add_to_bitws_ior_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_add_to_bitws_ior_cv(expression, rvalue, loperand);
		}
		else {
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().bit_intersect(loperand, roperand);
			this.infect(constraint, sem_mutation.get_assertions().dec_value(expression));
		}
	}
	private void arith_add_to_bitws_ior_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) { /** equivalent mutation **/ }
			else if(this.count_bit_ones(value) <= min_bit_ones) {
				this.infect(sem_mutation.get_assertions().diff_value(expression, -value));
			}
			else {
				constraint = sem_mutation.get_assertions().bit_intersect(roperand, lvalue);
				this.infect(constraint, sem_mutation.get_assertions().dec_value(expression));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_add_to_bitws_xor(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, 
			Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_add, loperand, roperand, COperator.bit_xor);
		}
		else if(lvalue != null) {
			this.arith_add_to_bitws_ior_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_add_to_bitws_ior_cv(expression, rvalue, loperand);
		}
		else {
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().bit_intersect(loperand, roperand);
			this.infect(constraint, sem_mutation.get_assertions().dec_value(expression));
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_add_to_bitws_lsh(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, 
			Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_add, loperand, roperand, COperator.left_shift);
		}
		else if(lvalue != null) {
			this.arith_add_to_bitws_lsh_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_add_to_bitws_lsh_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(roperand, 0);
			this.infect(constraint, sem_mutation.get_assertions().mut_value(expression));
			
			constraint = sem_mutation.get_assertions().in_range(roperand, "[0, 8]");
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void arith_add_to_bitws_lsh_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error1, error2, error3;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) {
				this.infect(sem_mutation.get_assertions().set_value(expression, 0));
			}
			else {
				constraint = sem_mutation.get_assertions().smaller_tn(roperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				this.infect(constraint, error1);
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(constraint, new SemanticAssertion[] { error2 });
				
				constraint = sem_mutation.get_assertions().in_range(roperand, "[0, 8]");
				error3 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(constraint, new SemanticAssertion[] { error3 });
			}
		}
		else throw new IllegalArgumentException("Invalid value");
	}
	private void arith_add_to_bitws_lsh_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 0) { /** equivalent mutation **/ }
			else if(value >= max_shifting) {
				this.infect(sem_mutation.get_assertions().set_value(expression, 0));
			}
			else if(value < 0) {
				this.infect(sem_mutation.get_assertions().mut_value(expression));
			}
			else {
				this.infect(sem_mutation.get_assertions().inc_value(expression));
			}
		}
		else throw new IllegalArgumentException("Invalid value");
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_add_to_bitws_rsh(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, 
			Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_add, loperand, roperand, COperator.righ_shift);
		}
		else if(lvalue != null) {
			this.arith_add_to_bitws_rsh_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_add_to_bitws_rsh_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error1, error2, error3;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(roperand, 0);
			this.infect(constraint, error1);
			
			constraint = sem_mutation.get_assertions().in_range(roperand, "[0, 8]");
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void arith_add_to_bitws_rsh_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error1, error2, error3;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) {
				this.infect(sem_mutation.get_assertions().set_value(expression, 0));
			}
			else {
				constraint = sem_mutation.get_assertions().smaller_tn(roperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				this.infect(constraint, error1);
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(constraint, new SemanticAssertion[] { error2 });
				
				constraint = sem_mutation.get_assertions().in_range(roperand, "[0, 8]");
				error3 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(constraint, new SemanticAssertion[] { error3 });
			}
		}
		else throw new IllegalArgumentException("Invalid value");
	}
	private void arith_add_to_bitws_rsh_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 0) { /** equivalent mutation **/ }
			else if(value >= max_shifting) {
				this.infect(sem_mutation.get_assertions().set_value(expression, 0));
			}
			else if(value < 0) {
				this.infect(sem_mutation.get_assertions().mut_value(expression));
			}
			else {
				this.infect(sem_mutation.get_assertions().dec_value(expression));
			}
		}
		else throw new IllegalArgumentException("Invalid value");
	}
	
	/* arith sub */
	/**
	 * (-, +)
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_sub_to_arith_add(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_sub, loperand, roperand, COperator.arith_add);
		}
		else if(rvalue != null) {
			this.arith_sub_to_arith_add_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error1, error2, error3;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error1 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
		}
	}
	private void arith_sub_to_arith_add_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion error2;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 0) { /** equivalent **/ }
			else {
				error2 = sem_mutation.get_assertions().diff_value(expression, 2 * value);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else if(rvalue instanceof Double) {
			double value = ((Double) rvalue).doubleValue();
			if(value == 0) { /** equivalent **/ }
			else {
				error2 = sem_mutation.get_assertions().diff_value(expression, 2 * value);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else throw new IllegalArgumentException("Invalid value");
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_sub_to_arith_mul(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_sub, loperand, roperand, COperator.arith_mul);
		}
		else if(lvalue != null) {
			this.arith_sub_to_arith_mul_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_sub_to_arith_mul_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			this.infect(new SemanticAssertion[] { error3 });
		}
	}
	private void arith_sub_to_arith_mul_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion error2;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) {
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == 1) {
				error2 = sem_mutation.get_assertions().neg_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				error2 = sem_mutation.get_assertions().diff_value(expression, -1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else if(lvalue instanceof Double) {
			double value = ((Double) lvalue).doubleValue();
			if(value == 0) {
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == 1) {
				error2 = sem_mutation.get_assertions().neg_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				error2 = sem_mutation.get_assertions().diff_value(expression, -1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private void arith_sub_to_arith_mul_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion error2;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 0) {
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == 1) {
				error2 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				error2 = sem_mutation.get_assertions().neg_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else if(rvalue instanceof Double) {
			double value = ((Double) rvalue).doubleValue();
			if(value == 0) {
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == 1) {
				error2 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				error2 = sem_mutation.get_assertions().neg_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_sub_to_arith_div(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_sub, loperand, roperand, COperator.arith_div);
		}
		else if(lvalue != null) {
			this.arith_sub_to_arith_div_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_sub_to_arith_div_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, sem_mutation.get_assertions().trapping());
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			this.infect(new SemanticAssertion[] { error3 });
		}
	}
	private void arith_sub_to_arith_div_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion error2;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0 || value == 1 || value == -1) {
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else if(lvalue instanceof Double) {
			double value = ((Double) lvalue).doubleValue();
			if(value == 0 || value == 1 || value == -1) {
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private void arith_sub_to_arith_div_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion error2;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 0) {
				this.infect(sem_mutation.get_assertions().trapping());
			}
			else if(value == 1) {
				error2 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				error2 = sem_mutation.get_assertions().neg_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else if(rvalue instanceof Double) {
			double value = ((Double) rvalue).doubleValue();
			if(value == 0) {
				this.infect(sem_mutation.get_assertions().trapping());
			}
			else if(value == 1) {
				error2 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				error2 = sem_mutation.get_assertions().neg_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_sub_to_arith_mod(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_sub, loperand, roperand, COperator.arith_mod);
		}
		else if(lvalue != null) {
			this.arith_sub_to_arith_mod_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_sub_to_arith_mod_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().trapping();
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, error3);
			
			constraint = sem_mutation.get_assertions().is_multiply(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().not_in_range(loperand, "[c, 2c)");
			this.infect(constraint, sem_mutation.get_assertions().mut_value(expression));
		}
	}
	private void arith_sub_to_arith_mod_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion error2;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0 || value == 1 || value == -1) {
				error2 = sem_mutation.get_assertions().set_value(expression, value);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				this.infect(sem_mutation.get_assertions().mut_value(expression));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private void arith_sub_to_arith_mod_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion constraint, error2;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 0) {
				this.infect(sem_mutation.get_assertions().trapping());
			}
			else if(value == 1 || value == -1) {
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				constraint = sem_mutation.get_assertions().not_in_range(loperand, "[c, 2c)");
				this.infect(constraint, sem_mutation.get_assertions().mut_value(expression));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_sub_to_bitws_and(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_sub, loperand, roperand, COperator.bit_and);
		}
		else if(lvalue != null) {
			this.arith_sub_to_bitws_and_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_sub_to_bitws_and_cv(expression, rvalue, loperand);
		}
		else {
			SemanticAssertion constraint, error1, error2;
			
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			this.infect(error1);
		}
	}
	private void arith_sub_to_bitws_and_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error2;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
				error2 = sem_mutation.get_assertions().set_value(expression, value);
				this.infect(constraint, new SemanticAssertion[] { error2 });
			}
			else if(this.count_bit_ones(value) <= min_bit_ones) {
				constraint = sem_mutation.get_assertions().bit_intersect(roperand, lvalue);
				error2 = sem_mutation.get_assertions().set_value(expression, value);
				this.infect(constraint, new SemanticAssertion[] { error2 });
			}
			else {
				this.infect(sem_mutation.get_assertions().mut_value(expression));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_sub_to_bitws_ior(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_sub, loperand, roperand, COperator.bit_or);
		}
		else if(lvalue != null) {
			this.arith_sub_to_bitws_ior_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_sub_to_bitws_ior_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2;
			error2 = sem_mutation.get_assertions().inc_value(expression);
			
			constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
		}
	}
	private void arith_sub_to_bitws_ior_cv(CirExpression expression, 
			Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion error2;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) {
				error2 = sem_mutation.get_assertions().neg_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				error2 = sem_mutation.get_assertions().set_value(expression, -1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid lvalue.");
		}
	}
	private void arith_sub_to_bitws_ior_vc(CirExpression expression, 
			CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion error2;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 0) { /** equivalent mutation **/ }
			else if(value > 0 && this.count_bit_ones(value) <= min_bit_ones){
				error2 = sem_mutation.get_assertions().diff_value(expression, 2 * value);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid lvalue.");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_sub_to_bitws_xor(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_sub, loperand, roperand, COperator.bit_xor);
		}
		else if(lvalue != null) {
			this.arith_sub_to_bitws_xor_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_sub_to_bitws_xor_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error1, error2;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().neg_value(expression);
			
			constraint = sem_mutation.get_assertions().not_subsuming(loperand, roperand);
			this.infect(constraint, error1);
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
		}
	}
	private void arith_sub_to_bitws_xor_cv(CirExpression expression, 
			Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion error2;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) {
				error2 = sem_mutation.get_assertions().neg_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) { /** equivalent mutant **/ }
			else {
				this.infect(sem_mutation.get_assertions().mut_value(expression));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private void arith_sub_to_bitws_xor_vc(CirExpression expression, 
			CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion constraint;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 0) { /** equivalent mutant **/ }
			else {
				constraint = sem_mutation.get_assertions().not_subsuming(loperand, rvalue);
				this.infect(constraint, sem_mutation.get_assertions().mut_value(expression));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_sub_to_bitws_lsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_sub, loperand, roperand, COperator.left_shift);
		}
		else if(lvalue != null) {
			this.arith_sub_to_bitws_lsh_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_sub_to_bitws_lsh_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error1, error2, error3;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(roperand, 0);
			this.infect(constraint, error1);
			
			constraint = sem_mutation.get_assertions().in_range(roperand, "[0, 8]");
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void arith_sub_to_bitws_lsh_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error1, error2, error3;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				constraint = sem_mutation.get_assertions().smaller_tn(roperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				this.infect(constraint, error1);
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(constraint, new SemanticAssertion[] { error2 });
				
				constraint = sem_mutation.get_assertions().in_range(roperand, "[0, 8]");
				error3 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(constraint, new SemanticAssertion[] { error3 });
			}
		}
		else throw new IllegalArgumentException("Invalid value");
	}
	private void arith_sub_to_bitws_lsh_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion error2;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 0) { /** equivalent mutation **/ }
			else if(value >= max_shifting) {
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value < 0) {
				this.infect(sem_mutation.get_assertions().mut_value(expression));
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else throw new IllegalArgumentException("Invalid value");
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_sub_to_bitws_rsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_sub, loperand, roperand, COperator.righ_shift);
		}
		else if(lvalue != null) {
			this.arith_sub_to_bitws_rsh_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_sub_to_bitws_rsh_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error1, error2, error3;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(roperand, 0);
			this.infect(constraint, error1);
			
			constraint = sem_mutation.get_assertions().in_range(roperand, "[0, 8]");
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void arith_sub_to_bitws_rsh_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error1, error2, error3;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) {
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				constraint = sem_mutation.get_assertions().smaller_tn(roperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				this.infect(constraint, error1);
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(constraint, new SemanticAssertion[] { error2 });
				
				constraint = sem_mutation.get_assertions().in_range(roperand, "[0, 8]");
				error3 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(constraint, new SemanticAssertion[] { error3 });
			}
		}
		else throw new IllegalArgumentException("Invalid value");
	}
	private void arith_sub_to_bitws_rsh_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion error2;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 0) { /** equivalent mutation **/ }
			else if(value >= max_shifting) {
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value < 0) {
				this.infect(sem_mutation.get_assertions().mut_value(expression));
			}
			else {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else throw new IllegalArgumentException("Invalid value");
	}
	
	/* arith mul */
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_mul_to_arith_add(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mul, loperand, roperand, COperator.arith_add);
		}
		else if(lvalue != null) {
			this.arith_mul_to_arith_add_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_mul_to_arith_add_cv(expression, rvalue, loperand);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			this.infect(new SemanticAssertion[] { error3 });
			constraint = sem_mutation.get_assertions().is_negative(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2, error3 });
		}
	}
	private void arith_mul_to_arith_add_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion error2;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 1) {
				error2 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				error2 = sem_mutation.get_assertions().neg_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else if(lvalue instanceof Double) {
			double value = ((Double) lvalue).doubleValue();
			if(value == 1) {
				error2 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				error2 = sem_mutation.get_assertions().neg_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_mul_to_arith_sub(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mul, loperand, roperand, COperator.arith_sub);
		}
		else if(lvalue != null) {
			this.arith_mul_to_arith_sub_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_mul_to_arith_sub_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			this.infect(new SemanticAssertion[] { error3 });
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2, error3 });
		}
	}
	private void arith_mul_to_arith_sub_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion error2;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 1) {
				error2 = sem_mutation.get_assertions().neg_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				error2 = sem_mutation.get_assertions().diff_value(expression, -1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else if(lvalue instanceof Double) {
			double value = ((Double) lvalue).doubleValue();
			if(value == 1) {
				error2 = sem_mutation.get_assertions().neg_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				error2 = sem_mutation.get_assertions().diff_value(expression, -1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private void arith_mul_to_arith_sub_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion error2;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 1) {
				error2 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				error2 = sem_mutation.get_assertions().neg_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else if(rvalue instanceof Double) {
			double value = ((Double) rvalue).doubleValue();
			if(value == 1) {
				error2 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				error2 = sem_mutation.get_assertions().neg_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_mul_to_arith_div(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mul, loperand, roperand, COperator.arith_div);
		}
		else if(lvalue != null) {
			this.arith_mul_to_arith_div_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_mul_to_arith_div_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, sem_mutation.get_assertions().trapping());
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, 1);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(roperand, -1);
			this.infect(constraint, new SemanticAssertion[] { error2 });
		}
	}
	private void arith_mul_to_arith_div_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion error2;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) { /** equivalent mutation **/ }
			else if(value > 0) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else if(lvalue instanceof Double) {
			double value = ((Double) lvalue).doubleValue();
			if(value == 0) { /** equivalent mutation **/ }
			else if(value > 0) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private void arith_mul_to_arith_div_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion error2;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 0) { 
				this.infect(sem_mutation.get_assertions().trapping());
			}
			else if(value == 1 || value == -1) { /** equivalent **/ }
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else if(rvalue instanceof Double) {
			double value = ((Double) rvalue).doubleValue();
			if(value == 0) { 
				this.infect(sem_mutation.get_assertions().trapping());
			}
			else if(value == 1 || value == -1) { /** equivalent **/ }
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_mul_to_arith_mod(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mul, loperand, roperand, COperator.arith_mod);
		}
		else if(lvalue != null) {
			this.arith_mul_to_arith_mod_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_mul_to_arith_mod_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, sem_mutation.get_assertions().trapping());
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 1);
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void arith_mul_to_arith_mod_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion error2;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) { /** equivalent mutant **/ }
			else if(value == 1 || value == -1) {
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else if(lvalue instanceof Double) {
			double value = ((Double) lvalue).doubleValue();
			if(value == 0) { /** equivalent mutant **/ }
			else if(value == 1 || value == -1) {
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private void arith_mul_to_arith_mod_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion error2;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 0) {
				this.infect(sem_mutation.get_assertions().trapping());
			}
			else if(value == 1 || value == -1) {
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else if(rvalue instanceof Double) {
			double value = ((Double) rvalue).doubleValue();
			if(value == 0) {
				this.infect(sem_mutation.get_assertions().trapping());
			}
			else if(value == 1 || value == -1) {
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_mul_to_bitws_and(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mul, loperand, roperand, COperator.bit_and);
		}
		else if(lvalue != null) {
			this.arith_mul_to_bitws_and_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_mul_to_bitws_and_cv(expression, rvalue, loperand);
		}
		else {
			SemanticAssertion cons1, cons2, error2;
			cons1 = sem_mutation.get_assertions().not_equals(loperand, 0);
			cons2 = sem_mutation.get_assertions().not_equals(roperand, 0);
			error2 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(new SemanticAssertion[] { cons1, cons2 },
					new SemanticAssertion[] { error2 });
		}
	}
	private void arith_mul_to_bitws_and_cv(CirExpression expression,
			Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error2;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) { /** equivalent mutant **/ }
			else if(value == 1) {
				constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
				error2 = sem_mutation.get_assertions().neg_value(expression);
				this.infect(constraint, new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(constraint, new SemanticAssertion[] { error2 });
			}
			else {
				constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(constraint, new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_mul_to_bitws_ior(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mul, loperand, roperand, COperator.bit_or);
		}
		else if(lvalue != null) {
			this.arith_mul_to_bitws_ior_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_mul_to_bitws_ior_cv(expression, rvalue, loperand);
		}
		else {
			SemanticAssertion error2;
			error2 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(new SemanticAssertion[] { error2 });
		}
	}
	private void arith_mul_to_bitws_ior_cv(CirExpression expression,
			Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion error2;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 1) {
				error2 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				error2 = sem_mutation.get_assertions().neg_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_mul_to_bitws_xor(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mul, loperand, roperand, COperator.bit_xor);
		}
		else if(lvalue != null) {
			this.arith_mul_to_bitws_xor_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_mul_to_bitws_xor_cv(expression, rvalue, loperand);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			this.infect(new SemanticAssertion[] { error3 });
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2, error3 });
		}
	}
	private void arith_mul_to_bitws_xor_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion error2;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 1) {
				error2 = sem_mutation.get_assertions().diff_value(expression, -1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				error2 = sem_mutation.get_assertions().diff_value(expression, -1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_mul_to_bitws_lsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mul, loperand, roperand, COperator.left_shift);
		}
		else if(lvalue != null) {
			this.arith_mul_to_bitws_lsh_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_mul_to_bitws_lsh_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			error2 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			error3 = sem_mutation.get_assertions().set_value(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void arith_mul_to_bitws_lsh_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error2;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) { /** equivalent mutation **/ }
			else if(value > 0) {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(constraint, new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(constraint, new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private void arith_mul_to_bitws_lsh_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion constraint, error2;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value < 0) {
				this.infect(sem_mutation.get_assertions().mut_value(expression));
			}
			else if(value >= max_shifting) {
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(constraint, new SemanticAssertion[] { error2 });
				
				constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(constraint, new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_mul_to_bitws_rsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mul, loperand, roperand, COperator.righ_shift);
		}
		else if(lvalue != null) {
			this.arith_mul_to_bitws_rsh_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_mul_to_bitws_rsh_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
			error2 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			error3 = sem_mutation.get_assertions().set_value(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void arith_mul_to_bitws_rsh_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error2;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) { /** equivalent mutation **/ }
			else if(value > 0) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(constraint, new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(constraint, new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private void arith_mul_to_bitws_rsh_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion constraint, error2;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value < 0) {
				this.infect(sem_mutation.get_assertions().mut_value(expression));
			}
			else if(value >= max_shifting) {
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(constraint, new SemanticAssertion[] { error2 });
				
				constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(constraint, new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	
	/* arith div */
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_div_to_arith_add(CirExpression expression, CirExpression loperand,
				CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_div, loperand, roperand, COperator.arith_add);
		}
		else if(lvalue != null) {
			this.arith_div_to_arith_add_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_div_to_arith_add_vc(expression, loperand, rvalue);
		}
		else { 
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void arith_div_to_arith_add_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion error2;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value >= 0) {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else if(lvalue instanceof Double) {
			double value = ((Double) lvalue).doubleValue();
			if(value >= 0) {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private void arith_div_to_arith_add_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion error2;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 1) {
				error2 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				error2 = sem_mutation.get_assertions().neg_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else if(rvalue instanceof Double) {
			double value = ((Double) rvalue).doubleValue();
			if(value == 1) {
				error2 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				error2 = sem_mutation.get_assertions().neg_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_div_to_arith_sub(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_div, loperand, roperand, COperator.arith_sub);
		}
		else if(lvalue != null) {
			this.arith_div_to_arith_sub_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_div_to_arith_sub_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			error2 = sem_mutation.get_assertions().diff_value(expression, -1);
			error3 = sem_mutation.get_assertions().set_value(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error2, error3 });
		}
	}
	private void arith_div_to_arith_sub_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion error2;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value >= 0) {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else if(lvalue instanceof Double) {
			double value = ((Double) lvalue).doubleValue();
			if(value >= 0) {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private void arith_div_to_arith_sub_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion error2;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 1) {
				error2 = sem_mutation.get_assertions().diff_value(expression, -1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				error2 = sem_mutation.get_assertions().neg_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else if(rvalue instanceof Double) {
			double value = ((Double) rvalue).doubleValue();
			if(value == 1) {
				error2 = sem_mutation.get_assertions().diff_value(expression, -1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				error2 = sem_mutation.get_assertions().neg_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_div_to_arith_mul(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_div, loperand, roperand, COperator.arith_mul);
		}
		else if(lvalue != null) {
			this.arith_div_to_arith_mul_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_div_to_arith_mul_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion cons1, cons2, error1, error2, error3;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			cons1 = sem_mutation.get_assertions().not_equals(loperand, 0);
			cons2 = sem_mutation.get_assertions().not_equals(roperand, 1);
			this.infect(new SemanticAssertion[] { cons1, cons2 }, error1);
			
			cons1 = sem_mutation.get_assertions().greater_tn(expression, 0);
			this.infect(cons1, new SemanticAssertion[] { error2 });
			
			cons2 = sem_mutation.get_assertions().smaller_tn(expression, 0);
			this.infect(cons2, new SemanticAssertion[] { error3 });
		}
	}
	private void arith_div_to_arith_mul_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion error2;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) { /** equivalent mutant **/ }
			else if(value > 0) {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else if(lvalue instanceof Double) {
			double value = ((Double) lvalue).doubleValue();
			if(value == 0) { /** equivalent mutant **/ }
			else if(value > 0) {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private void arith_div_to_arith_mul_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion error2;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 1 || value == -1) { /** equivalent mutant **/ }
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else if(rvalue instanceof Double) {
			double value = ((Double) rvalue).doubleValue();
			if(value == 1 || value == -1) { /** equivalent mutant **/ }
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_div_to_arith_mod(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_div, loperand, roperand, COperator.arith_mod);
		}
		else {
			SemanticAssertion constraint, error1, error2;
			
			constraint = sem_mutation.get_assertions().not_equals(loperand, 0);
			error1 = sem_mutation.get_assertions().mut_value(expression);
			this.infect(constraint, error1);
			
			constraint = sem_mutation.get_assertions().is_multiply(loperand, roperand);
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_div_to_bitws_and(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_div, loperand, roperand, COperator.bit_and);
		}
		else if(lvalue != null) {
			this.arith_div_to_bitws_and_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_div_to_bitws_and_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().not_equals(loperand, 0);
			this.infect(constraint, sem_mutation.get_assertions().mut_value(expression));
		}
	}
	private void arith_div_to_bitws_and_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion error2;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) { /** equivalent mutant **/ }
			else if(value == 1) {
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				this.infect(sem_mutation.get_assertions().mut_value(expression));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private void arith_div_to_bitws_and_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion error2;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 1) {
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else if(value == -1) {
				error2 = sem_mutation.get_assertions().neg_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				this.infect(sem_mutation.get_assertions().mut_value(expression));
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_div_to_bitws_ior(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_div, loperand, roperand, COperator.bit_or);
		}
		else if(lvalue != null) {
			this.arith_div_to_arith_add_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_div_to_arith_add_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_div_to_bitws_xor(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_div, loperand, roperand, COperator.bit_xor);
		}
		else if(lvalue != null) {
			this.arith_div_to_arith_sub_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_div_to_arith_sub_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			error2 = sem_mutation.get_assertions().diff_value(expression, -1);
			error3 = sem_mutation.get_assertions().set_value(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error2, error3 });
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_div_to_bitws_lsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_div, loperand, roperand, COperator.left_shift);
		}
		else if(lvalue != null) {
			this.arith_div_to_bitws_lsh_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_div_to_bitws_lsh_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			error2 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			error3 = sem_mutation.get_assertions().set_value(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void arith_div_to_bitws_lsh_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error2;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) { /** equivalent mutation **/ }
			else if(value > 0) {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(constraint, new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(constraint, new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private void arith_div_to_bitws_lsh_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion constraint, error2;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value < 0) {
				this.infect(sem_mutation.get_assertions().mut_value(expression));
			}
			else if(value >= max_shifting) {
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(constraint, new SemanticAssertion[] { error2 });
				
				constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(constraint, new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_div_to_bitws_rsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_div, loperand, roperand, COperator.righ_shift);
		}
		else if(lvalue != null) {
			this.arith_div_to_bitws_rsh_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_div_to_bitws_rsh_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
			error2 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			error3 = sem_mutation.get_assertions().set_value(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void arith_div_to_bitws_rsh_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error2;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) { /** equivalent mutation **/ }
			else if(value > 0) {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(constraint, new SemanticAssertion[] { error2 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(constraint, new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private void arith_div_to_bitws_rsh_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion constraint, error2;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value < 0) {
				this.infect(sem_mutation.get_assertions().mut_value(expression));
			}
			else if(value >= max_shifting) {
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(new SemanticAssertion[] { error2 });
			}
			else {
				constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(constraint, new SemanticAssertion[] { error2 });
				
				constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(constraint, new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	
	/* arith mod */
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_mod_to_arith_add(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mod, loperand, roperand, COperator.arith_add);
		}
		else {
			SemanticAssertion error2;
			error2 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(new SemanticAssertion[] { error2 });
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_mod_to_arith_sub(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mod, loperand, roperand, COperator.arith_sub);
		}
		else {
			SemanticAssertion constraint, error1, error2, error3;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().not_in_range(loperand, "[c, 2c)");
			this.infect(constraint, error1);
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_mod_to_arith_mul(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mod, loperand, roperand, COperator.arith_mul);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_mod_to_arith_div(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mod, loperand, roperand, COperator.arith_div);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_mod_to_bitws_and(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mod, loperand, roperand, COperator.bit_and);
		}
		else {
			SemanticAssertion constraint, state_error;
			constraint = sem_mutation.get_assertions().not_equals(loperand, 0);
			state_error = sem_mutation.get_assertions().mut_value(expression);
			this.infect(constraint, state_error);
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_mod_to_bitws_ior(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mod, loperand, roperand, COperator.bit_or);
		}
		else {
			SemanticAssertion error2;
			error2 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(new SemanticAssertion[] { error2 });
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_mod_to_bitws_xor(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mod, loperand, roperand, COperator.bit_xor);
		}
		else {
			SemanticAssertion constraint, error1, error2, error3;
			error1 = sem_mutation.get_assertions().mut_value(expression);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().not_in_range(loperand, "[c, 2c)");
			this.infect(constraint, error1);
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_mod_to_bitws_lsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mod, loperand, roperand, COperator.left_shift);
		}
		else if(rvalue != null) {
			this.arith_mod_to_bitws_lsh_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().inc_value(expression);
			error3 = sem_mutation.get_assertions().dec_value(expression);
			error4 = sem_mutation.get_assertions().set_value(expression, 0);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void arith_mod_to_bitws_lsh_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			SemanticAssertion error1, error2, error4;
			if(value < 0) {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				this.infect(new SemanticAssertion[] { error1 });
			}
			else if(value >= max_shifting) {
				error4 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(new SemanticAssertion[] { error4 });
			}
			else {
				error2 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_mod_to_bitws_rsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mod, loperand, roperand, COperator.righ_shift);
		}
		else if(rvalue != null) {
			this.arith_mod_to_bitws_rsh_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().dec_value(expression);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().set_value(expression, 0);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void arith_mod_to_bitws_rsh_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			SemanticAssertion error1, error2, error4;
			if(value < 0) {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				this.infect(new SemanticAssertion[] { error1 });
			}
			else if(value >= max_shifting) {
				error4 = sem_mutation.get_assertions().set_value(expression, 0);
				this.infect(new SemanticAssertion[] { error4 });
			}
			else {
				error2 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	
	/* assign */
	private void arith_add_to_assign(CirExpression expression, CirExpression loperand,
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
			SemanticAssertion constraint, error1, error2;
			error1 = sem_mutation.get_assertions().inc_value(expression);
			error2 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
			this.infect(constraint, error2);
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			this.infect(constraint, error1);
		}
	}
	private void arith_sub_to_assign(CirExpression expression, CirExpression loperand,
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
			SemanticAssertion constraint, error1, error2;
			error1 = sem_mutation.get_assertions().inc_value(expression);
			error2 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
			this.infect(constraint, error2);
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
			this.infect(constraint, error1);
		}
	}
	private void arith_mul_to_assign(CirExpression expression, CirExpression loperand,
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
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().greater_tn(loperand, 1);
			infect(constraint, sem_mutation.get_assertions().dec_value(expression));
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, 1);
			infect(constraint, sem_mutation.get_assertions().inc_value(expression));
		}
	}
	private void arith_div_to_assign(CirExpression expression, CirExpression loperand,
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
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().not_equals(loperand, 0);
			infect(constraint, sem_mutation.get_assertions().mut_value(expression));
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			infect(constraint, sem_mutation.get_assertions().trapping());
		}
	}
	private void arith_mod_to_assign(CirExpression expression, CirExpression loperand,
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
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().is_multiply(loperand, roperand);
			infect(constraint, sem_mutation.get_assertions().inc_value(expression));
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			infect(constraint, sem_mutation.get_assertions().trapping());
		}
	}
	
}
