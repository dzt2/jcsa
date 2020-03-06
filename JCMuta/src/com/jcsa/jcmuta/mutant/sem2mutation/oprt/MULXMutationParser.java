package com.jcsa.jcmuta.mutant.sem2mutation.oprt;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class MULXMutationParser extends OXXNMutationParser {
	
	@Override
	protected void generate_infections(AstMutation ast_mutation, CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		switch(ast_mutation.get_mutation_operator()) {
		case arith_mul_to_arith_add:
			this.arith_mul_to_arith_add(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_to_arith_sub:
			this.arith_mul_to_arith_sub(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_to_arith_div:
			this.arith_mul_to_arith_div(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_to_arith_mod:
			this.arith_mul_to_arith_mod(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_to_bitws_and:
			this.arith_mul_to_bitws_and(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_to_bitws_ior:
			this.arith_mul_to_bitws_ior(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_to_bitws_xor:
			this.arith_mul_to_bitws_xor(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_to_bitws_lsh:
			this.arith_mul_to_bitws_lsh(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_to_bitws_rsh:
			this.arith_mul_to_bitws_rsh(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_to_logic_and:
			this.arith_mul_to_logic_and(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_to_logic_ior:
			this.arith_mul_to_logic_ior(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_to_greater_tn:
			this.arith_mul_to_greater_tn(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_to_greater_eq:
			this.arith_mul_to_greater_eq(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_to_smaller_tn:
			this.arith_mul_to_smaller_tn(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_to_smaller_eq:
			this.arith_mul_to_smaller_eq(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_to_equal_with:
			this.arith_mul_to_equal_with(expression, loperand, roperand, lvalue, rvalue); break;
		case arith_mul_to_not_equals:
			this.arith_mul_to_not_equals(expression, loperand, roperand, lvalue, rvalue); break;
		default: throw new IllegalArgumentException("Invalid mutation operator.");
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
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_mul_to_logic_and(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mul, loperand, roperand, COperator.logic_and);
		}
		else if(lvalue != null) {
			this.arith_mul_to_logic_and_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_mul_to_logic_and_cv(expression, rvalue, loperand);
		}
		else {
			SemanticAssertion cons1, cons2, error2;
			cons1 = sem_mutation.get_assertions().not_equals(loperand, 0);
			cons2 = sem_mutation.get_assertions().not_equals(roperand, 0);
			error2 = sem_mutation.get_assertions().set_value(expression, 1);
			this.infect(new SemanticAssertion[] { cons1, cons2 },
					new SemanticAssertion[] { error2 });
			
			cons1 = sem_mutation.get_assertions().greater_tn(expression, 0);
			error2 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(cons1, new SemanticAssertion[] { error2 });
			
			cons1 = sem_mutation.get_assertions().smaller_tn(expression, 0);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(cons1, new SemanticAssertion[] { error2 });
		}
	}
	private void arith_mul_to_logic_and_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error2, error3;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) { /** equivalent mutant **/ }
			if(value == 1 || value == -1) {
				constraint = sem_mutation.get_assertions().not_equals(roperand, value);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2, error3 });
			}
			else {
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2, error3 });
			}
		}
		else if(lvalue instanceof Double) {
			double value = ((Double) lvalue).doubleValue();
			if(value == 0) { /** equivalent mutant **/ }
			if(value == 1 || value == -1) {
				constraint = sem_mutation.get_assertions().not_equals(roperand, value);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error2 });
			}
			else if(value > 1) {
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error2, error3 });
			}
			else {
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error2, error3 });
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
	private void arith_mul_to_logic_ior(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mul, loperand, roperand, COperator.logic_or);
		}
		else if(lvalue != null) {
			this.arith_mul_to_logic_ior_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_mul_to_logic_ior_cv(expression, rvalue, loperand);
		}
		else {
			SemanticAssertion cons1, cons2, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 1);
			error3 = sem_mutation.get_assertions().diff_value(expression, 1);
			
			cons1 = sem_mutation.get_assertions().equal_with(loperand, 0);
			cons2 = sem_mutation.get_assertions().not_equals(roperand, 0);
			this.infect(new SemanticAssertion[] { cons1, cons2 },
						new SemanticAssertion[] { error2, error3 });
			
			cons1 = sem_mutation.get_assertions().not_equals(loperand, 0);
			cons2 = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(new SemanticAssertion[] { cons1, cons2 },
						new SemanticAssertion[] { error2, error3 });
			
			cons1 = sem_mutation.get_assertions().not_equals(loperand, 0);
			cons2 = sem_mutation.get_assertions().not_equals(roperand, 0);
			this.infect(cons1, new SemanticAssertion[] { error2 });
			this.infect(cons2, new SemanticAssertion[] { error2 });
		}
	}
	private void arith_mul_to_logic_ior_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error1, error2, error3;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else if(value == 1 || value == -1) {
				constraint = sem_mutation.get_assertions().not_equals(roperand, value);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
			}
			else if(value > 1) {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error1, error2, error3 });
			}
		}
		else if(lvalue instanceof Double) {
			double value = ((Double) lvalue).doubleValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else if(value == 1 || value == -1) {
				constraint = sem_mutation.get_assertions().not_equals(roperand, value);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
			}
			else if(value > 1) {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().dec_value(expression);
				this.infect(new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().inc_value(expression);
				this.infect(new SemanticAssertion[] { error1, error2, error3 });
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
	private void arith_mul_to_greater_tn(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mul, loperand, roperand, COperator.greater_tn);
		}
		else if(lvalue != null) {
			this.arith_mul_to_greater_tn_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_mul_to_greater_tn_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			error2 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
		}
	}
	private void arith_mul_to_greater_tn_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error1, error2, error3;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().smaller_tn(roperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				error3 = sem_mutation.get_assertions().set_value(expression, 1);
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
				
				constraint = sem_mutation.get_assertions().equal_with(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
				
				constraint = sem_mutation.get_assertions().smaller_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
			}
		}
		else if(lvalue instanceof Double) {
			double value = ((Double) lvalue).doubleValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().smaller_tn(roperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				error3 = sem_mutation.get_assertions().set_value(expression, 1);
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
				
				constraint = sem_mutation.get_assertions().equal_with(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
				
				constraint = sem_mutation.get_assertions().smaller_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private void arith_mul_to_greater_tn_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion constraint, error1, error2, error3;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				error3 = sem_mutation.get_assertions().set_value(expression, 1);
				
				constraint = sem_mutation.get_assertions().smaller_tn(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
				
				constraint = sem_mutation.get_assertions().equal_with(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
				
				constraint = sem_mutation.get_assertions().greater_tn(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
			}
		}
		else if(rvalue instanceof Double) {
			double value = ((Double) rvalue).doubleValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				error3 = sem_mutation.get_assertions().set_value(expression, 1);
				
				constraint = sem_mutation.get_assertions().smaller_tn(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
				
				constraint = sem_mutation.get_assertions().equal_with(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
				
				constraint = sem_mutation.get_assertions().greater_tn(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
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
	private void arith_mul_to_greater_eq(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mul, loperand, roperand, COperator.greater_eq);
		}
		else if(lvalue != null) {
			this.arith_mul_to_greater_eq_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_mul_to_greater_eq_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			error2 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
		}
	}
	private void arith_mul_to_greater_eq_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error1, error2, error3;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().smaller_tn(roperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				error3 = sem_mutation.get_assertions().set_value(expression, 1);
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
				
				constraint = sem_mutation.get_assertions().equal_with(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
				
				constraint = sem_mutation.get_assertions().smaller_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
			}
		}
		else if(lvalue instanceof Double) {
			double value = ((Double) lvalue).doubleValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().smaller_tn(roperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				error3 = sem_mutation.get_assertions().set_value(expression, 1);
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
				
				constraint = sem_mutation.get_assertions().equal_with(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
				
				constraint = sem_mutation.get_assertions().smaller_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private void arith_mul_to_greater_eq_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion constraint, error1, error2, error3;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				error3 = sem_mutation.get_assertions().set_value(expression, 1);
				
				constraint = sem_mutation.get_assertions().smaller_tn(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
				
				constraint = sem_mutation.get_assertions().equal_with(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
				
				constraint = sem_mutation.get_assertions().greater_tn(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
			}
		}
		else if(rvalue instanceof Double) {
			double value = ((Double) rvalue).doubleValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().greater_tn(loperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				error3 = sem_mutation.get_assertions().set_value(expression, 1);
				
				constraint = sem_mutation.get_assertions().smaller_tn(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
				
				constraint = sem_mutation.get_assertions().equal_with(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
				
				constraint = sem_mutation.get_assertions().greater_tn(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	/***
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_mul_to_smaller_tn(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mul, loperand, roperand, COperator.smaller_tn);
		}
		else if(lvalue != null) {
			this.arith_mul_to_smaller_tn_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_mul_to_smaller_tn_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			error2 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
		}
	}
	private void arith_mul_to_smaller_tn_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error1, error2, error3;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().greater_tn(roperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				error3 = sem_mutation.get_assertions().set_value(expression, 1);
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
				
				constraint = sem_mutation.get_assertions().equal_with(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
				
				constraint = sem_mutation.get_assertions().smaller_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
			}
		}
		else if(lvalue instanceof Double) {
			double value = ((Double) lvalue).doubleValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().greater_tn(roperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				error3 = sem_mutation.get_assertions().set_value(expression, 1);
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
				
				constraint = sem_mutation.get_assertions().equal_with(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
				
				constraint = sem_mutation.get_assertions().smaller_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private void arith_mul_to_smaller_tn_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion constraint, error1, error2, error3;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				error3 = sem_mutation.get_assertions().set_value(expression, 1);
				
				constraint = sem_mutation.get_assertions().smaller_tn(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
				
				constraint = sem_mutation.get_assertions().equal_with(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
				
				constraint = sem_mutation.get_assertions().greater_tn(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
			}
		}
		else if(rvalue instanceof Double) {
			double value = ((Double) rvalue).doubleValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				error3 = sem_mutation.get_assertions().set_value(expression, 1);
				
				constraint = sem_mutation.get_assertions().smaller_tn(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
				
				constraint = sem_mutation.get_assertions().equal_with(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
				
				constraint = sem_mutation.get_assertions().greater_tn(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
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
	private void arith_mul_to_smaller_eq(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mul, loperand, roperand, COperator.smaller_eq);
		}
		else if(lvalue != null) {
			this.arith_mul_to_smaller_eq_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_mul_to_smaller_eq_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			error2 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
		}
	}
	private void arith_mul_to_smaller_eq_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error1, error2, error3;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().greater_tn(roperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				error3 = sem_mutation.get_assertions().set_value(expression, 1);
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
				
				constraint = sem_mutation.get_assertions().equal_with(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
				
				constraint = sem_mutation.get_assertions().smaller_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
			}
		}
		else if(lvalue instanceof Double) {
			double value = ((Double) lvalue).doubleValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().greater_tn(roperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				error3 = sem_mutation.get_assertions().set_value(expression, 1);
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
				
				constraint = sem_mutation.get_assertions().equal_with(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
				
				constraint = sem_mutation.get_assertions().smaller_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	private void arith_mul_to_smaller_eq_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion constraint, error1, error2, error3;
		if(rvalue instanceof Long) {
			long value = ((Long) rvalue).longValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				error3 = sem_mutation.get_assertions().set_value(expression, 1);
				
				constraint = sem_mutation.get_assertions().smaller_tn(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
				
				constraint = sem_mutation.get_assertions().equal_with(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
				
				constraint = sem_mutation.get_assertions().greater_tn(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
			}
		}
		else if(rvalue instanceof Double) {
			double value = ((Double) rvalue).doubleValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().smaller_tn(loperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				error3 = sem_mutation.get_assertions().set_value(expression, 1);
				
				constraint = sem_mutation.get_assertions().smaller_tn(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
				
				constraint = sem_mutation.get_assertions().equal_with(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
				
				constraint = sem_mutation.get_assertions().greater_tn(loperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
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
	private void arith_mul_to_equal_with(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mul, loperand, roperand, COperator.equal_with);
		}
		else if(lvalue != null) {
			this.arith_mul_to_equal_with_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_mul_to_equal_with_cv(expression, rvalue, loperand);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			error2 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
		}
	}
	private void arith_mul_to_equal_with_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error1, error2, error3;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				error3 = sem_mutation.get_assertions().set_value(expression, 1);
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
				
				constraint = sem_mutation.get_assertions().equal_with(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
				
				constraint = sem_mutation.get_assertions().smaller_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
			}
		}
		else if(lvalue instanceof Double) {
			double value = ((Double) lvalue).doubleValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 0);
				error3 = sem_mutation.get_assertions().set_value(expression, 1);
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
				
				constraint = sem_mutation.get_assertions().equal_with(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
				
				constraint = sem_mutation.get_assertions().smaller_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
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
	private void arith_mul_to_not_equals(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_mul, loperand, roperand, COperator.not_equals);
		}
		else if(lvalue != null) {
			this.arith_mul_to_not_equals_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_mul_to_not_equals_cv(expression, rvalue, loperand);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 1);
			error3 = sem_mutation.get_assertions().set_value(expression, 0);
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			error2 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			error2 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error2 });
		}
	}
	private void arith_mul_to_not_equals_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error1, error2, error3;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().set_value(expression, 0);
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
				
				constraint = sem_mutation.get_assertions().equal_with(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
				
				constraint = sem_mutation.get_assertions().smaller_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
			}
		}
		else if(lvalue instanceof Double) {
			double value = ((Double) lvalue).doubleValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().diff_value(expression, 1);
				this.infect(constraint, new SemanticAssertion[] { error1, error2, error3 });
			}
			else {
				error1 = sem_mutation.get_assertions().mut_value(expression);
				error2 = sem_mutation.get_assertions().set_value(expression, 1);
				error3 = sem_mutation.get_assertions().set_value(expression, 0);
				
				constraint = sem_mutation.get_assertions().greater_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
				
				constraint = sem_mutation.get_assertions().equal_with(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error3 });
				
				constraint = sem_mutation.get_assertions().smaller_tn(roperand, value);
				this.infect(constraint, new SemanticAssertion[] { error1, error2 });
			}
		}
		else {
			throw new IllegalArgumentException("Invalid value");
		}
	}
	
}
