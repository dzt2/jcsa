package com.jcsa.jcmuta.mutant.sem2mutation.oprt;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class MODXMutationParser extends OXXNMutationParser {

	@Override
	protected void generate_infections(AstMutation ast_mutation, CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		switch(ast_mutation.get_mutation_operator()) {
		case arith_mod_to_arith_add: 	this.arith_mod_to_arith_add(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_to_arith_sub: 	this.arith_mod_to_arith_sub(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_to_arith_mul: 	this.arith_mod_to_arith_mul(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_to_arith_div: 	this.arith_mod_to_arith_div(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_to_bitws_and: 	this.arith_mod_to_bitws_and(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_to_bitws_ior: 	this.arith_mod_to_bitws_ior(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_to_bitws_xor: 	this.arith_mod_to_bitws_xor(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_to_bitws_lsh: 	this.arith_mod_to_bitws_lsh(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_to_bitws_rsh: 	this.arith_mod_to_bitws_rsh(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_to_logic_and: 	this.arith_mod_to_logic_and(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_to_logic_ior: 	this.arith_mod_to_logic_ior(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_to_greater_tn: 	this.arith_mod_to_greater_tn(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_to_greater_eq: 	this.arith_mod_to_greater_eq(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_to_smaller_tn: 	this.arith_mod_to_smaller_tn(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_to_smaller_eq: 	this.arith_mod_to_smaller_eq(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_to_equal_with: 	this.arith_mod_to_equal_with(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mod_to_not_equals: 	this.arith_mod_to_not_equals(expression, loperand, roperand, lvalue, rvalue); break;
		default: throw new IllegalArgumentException("Invalid mutation operator");
		}
	}
	
	/* implementation */
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
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_mod_to_logic_and(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mod, loperand, roperand, COperator.logic_and);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().is_multiply(loperand, roperand);
			error3 = sem_mutation.get_assertions().diff_value(expression, 1);
			this.infect(constraint, new SemanticAssertion[] { error2, error3 });
			
			constraint = sem_mutation.get_assertions().not_equals(loperand, 0);
			error3 = sem_mutation.get_assertions().dec_value(expression);
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
	private void arith_mod_to_logic_ior(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mod, loperand, roperand, COperator.logic_or);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().is_multiply(loperand, roperand);
			error3 = sem_mutation.get_assertions().diff_value(expression, 1);
			this.infect(constraint, new SemanticAssertion[] { error2, error3 });
			
			constraint = sem_mutation.get_assertions().not_equals(loperand, 0);
			error3 = sem_mutation.get_assertions().dec_value(expression);
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
	private void arith_mod_to_greater_tn(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mod, loperand, roperand, COperator.greater_tn);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error3, error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			error4 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2, error4 });
			
			constraint = sem_mutation.get_assertions().is_multiply(loperand, roperand);
			error4 = sem_mutation.get_assertions().diff_value(expression, 1);
			this.infect(constraint, new SemanticAssertion[] { error3, error4 });
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
	private void arith_mod_to_greater_eq(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mod, loperand, roperand, COperator.greater_eq);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error3, error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			error4 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2, error4 });
			
			constraint = sem_mutation.get_assertions().is_multiply(loperand, roperand);
			error4 = sem_mutation.get_assertions().diff_value(expression, 1);
			this.infect(constraint, new SemanticAssertion[] { error3, error4 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			error4 = sem_mutation.get_assertions().diff_value(expression, 1);
			this.infect(constraint, new SemanticAssertion[] { error3, error4 });
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
	private void arith_mod_to_smaller_tn(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mod, loperand, roperand, COperator.smaller_tn);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error3, error4 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			error4 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2, error4 });
			
			constraint = sem_mutation.get_assertions().is_multiply(loperand, roperand);
			error4 = sem_mutation.get_assertions().diff_value(expression, 1);
			this.infect(constraint, new SemanticAssertion[] { error3, error4 });
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
	private void arith_mod_to_smaller_eq(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mod, loperand, roperand, COperator.smaller_eq);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error3, error4 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			error4 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2, error4 });
			
			constraint = sem_mutation.get_assertions().is_multiply(loperand, roperand);
			error4 = sem_mutation.get_assertions().diff_value(expression, 1);
			this.infect(constraint, new SemanticAssertion[] { error3, error4 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			error4 = sem_mutation.get_assertions().diff_value(expression, 1);
			this.infect(constraint, new SemanticAssertion[] { error3, error4 });
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
	private void arith_mod_to_equal_with(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mod, loperand, roperand, COperator.equal_with);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().not_equals(loperand, roperand);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2, error4 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			error4 = sem_mutation.get_assertions().diff_value(expression, 1);
			this.infect(constraint, new SemanticAssertion[] { error3, error4 });
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
	private void arith_mod_to_not_equals(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mod, loperand, roperand, COperator.equal_with);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().not_equals(loperand, roperand);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error3, error4 });
			
			constraint = sem_mutation.get_assertions().is_multiply(loperand, roperand);
			error4 = sem_mutation.get_assertions().diff_value(expression, 1);
			this.infect(constraint, new SemanticAssertion[] { error2, error4 });
		}
	}
	
}
