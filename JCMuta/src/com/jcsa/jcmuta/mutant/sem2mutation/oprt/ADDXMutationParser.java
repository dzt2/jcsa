package com.jcsa.jcmuta.mutant.sem2mutation.oprt;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class ADDXMutationParser extends OXXNMutationParser {
	
	@Override
	protected void generate_infections(AstMutation ast_mutation,
			CirExpression expression, CirExpression loperand, 
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		switch(ast_mutation.get_mutation_operator()) {
		case arith_add_to_arith_sub:	
			this.arith_add_to_arith_sub(expression, loperand, roperand, lvalue, rvalue); 	break;
		case arith_add_to_arith_mul:
			this.arith_add_to_arith_mul(expression, loperand, roperand, lvalue, rvalue); 	break;
		case arith_add_to_arith_div:
			this.arith_add_to_arith_div(expression, loperand, roperand, lvalue, rvalue); 	break;
		case arith_add_to_arith_mod:
			this.arith_add_to_arith_mod(expression, loperand, roperand, lvalue, rvalue); 	break;
		case arith_add_to_bitws_and:
			this.arith_add_to_bitws_and(expression, loperand, roperand, lvalue, rvalue); 	break;
		case arith_add_to_bitws_ior:
			this.arith_add_to_bitws_ior(expression, loperand, roperand, lvalue, rvalue); 	break;
		case arith_add_to_bitws_xor:
			this.arith_add_to_bitws_xor(expression, loperand, roperand, lvalue, rvalue); 	break;
		case arith_add_to_bitws_lsh:
			this.arith_add_to_bitws_lsh(expression, loperand, roperand, lvalue, rvalue); 	break;
		case arith_add_to_bitws_rsh:
			this.arith_add_to_bitws_rsh(expression, loperand, roperand, lvalue, rvalue); 	break;
		case arith_add_to_logic_and:
			this.arith_add_to_logic_and(expression, loperand, roperand, lvalue, rvalue); 	break;
		case arith_add_to_logic_ior:
			this.arith_add_to_logic_ior(expression, loperand, roperand, lvalue, rvalue); 	break;
		case arith_add_to_greater_tn:
			this.arith_add_to_greater_tn(expression, loperand, roperand, lvalue, rvalue); 	break;
		case arith_add_to_greater_eq:
			this.arith_add_to_greater_eq(expression, loperand, roperand, lvalue, rvalue); 	break;
		case arith_add_to_smaller_tn:
			this.arith_add_to_smaller_tn(expression, loperand, roperand, lvalue, rvalue);	break;
		case arith_add_to_smaller_eq:
			this.arith_add_to_smaller_eq(expression, loperand, roperand, lvalue, rvalue);	break;
		case arith_add_to_equal_with:
			this.arith_add_to_equal_with(expression, loperand, roperand, lvalue, rvalue); 	break;
		case arith_add_to_not_equals:
			this.arith_add_to_not_equals(expression, loperand, roperand, lvalue, rvalue); 	break;
		default: throw new IllegalArgumentException("Invalid mutation operator.");
		}
	}
	
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
	/**
	 * 
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param lvalue
	 * @param rvalue
	 * @throws Exception
	 */
	private void arith_add_to_logic_and(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, 
			Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_add, loperand, roperand, COperator.logic_and);
		}
		else if(lvalue != null) {
			this.arith_add_to_logic_and_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_add_to_logic_and_cv(expression, rvalue, loperand);
		}
		else {
			SemanticAssertion constraint, constraint2, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().not_equals(loperand, 0);
			constraint2= sem_mutation.get_assertions().not_equals(roperand, 0);
			this.infect(
					new SemanticAssertion[] { constraint, constraint2 }, 
					new SemanticAssertion[] { error3 });
		}
	}
	private void arith_add_to_logic_and_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) {
				this.infect(sem_mutation.get_assertions().set_value(expression, 0));
			}
			else {
				constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
				this.infect(constraint, sem_mutation.get_assertions().set_value(expression, 0));
				
				constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
				this.infect(constraint, sem_mutation.get_assertions().set_value(expression, 1));
			}
		}
		else if(lvalue instanceof Double) {
			double value = ((Double) lvalue).doubleValue();
			if(value == 0) {
				this.infect(sem_mutation.get_assertions().set_value(expression, 0));
			}
			else {
				constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
				this.infect(constraint, sem_mutation.get_assertions().set_value(expression, 0));
				
				constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
				this.infect(constraint, sem_mutation.get_assertions().set_value(expression, 1));
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
	private void arith_add_to_logic_ior(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, 
			Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_add, loperand, roperand, COperator.logic_or);
		}
		else if(lvalue != null) {
			this.arith_add_to_logic_ior_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_add_to_logic_ior_cv(expression, rvalue, loperand);
		}
		else {
			SemanticAssertion constraint, error2;
			error2 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().not_equals(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
		}
	}
	private void arith_add_to_logic_ior_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint;
		if(lvalue instanceof Long) {
			long value = ((Long) lvalue).longValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
				this.infect(constraint, sem_mutation.get_assertions().set_value(expression, 1));
			}
			else {
				this.infect(sem_mutation.get_assertions().set_value(expression, 1));
			}
		}
		else if(lvalue instanceof Double) {
			double value = ((Double) lvalue).doubleValue();
			if(value == 0) {
				constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
				this.infect(constraint, sem_mutation.get_assertions().set_value(expression, 1));
			}
			else {
				this.infect(sem_mutation.get_assertions().set_value(expression, 1));
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
	private void arith_add_to_greater_tn(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, 
			Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_add, loperand, roperand, COperator.greater_tn);
		}
		else if(lvalue != null) {
			this.arith_add_to_greater_tn_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_add_to_greater_tn_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
		}
	}
	private void arith_add_to_greater_tn_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error2, error3;
		error2 = sem_mutation.get_assertions().set_value(expression, 0);
		error3 = sem_mutation.get_assertions().set_value(expression, 1);
		
		constraint = sem_mutation.get_assertions().smaller_tn(roperand, lvalue);
		this.infect(constraint, new SemanticAssertion[] { error3 });
		
		constraint = sem_mutation.get_assertions().equal_with(roperand, lvalue);
		this.infect(constraint, new SemanticAssertion[] { error2 });
		
		constraint = sem_mutation.get_assertions().greater_tn(roperand, lvalue);
		this.infect(constraint, new SemanticAssertion[] { error2 });
	}
	private void arith_add_to_greater_tn_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion constraint, error2, error3;
		error2 = sem_mutation.get_assertions().set_value(expression, 0);
		error3 = sem_mutation.get_assertions().set_value(expression, 1);
		
		constraint = sem_mutation.get_assertions().smaller_tn(loperand, rvalue);
		this.infect(constraint, new SemanticAssertion[] { error2 });
		
		constraint = sem_mutation.get_assertions().equal_with(loperand, rvalue);
		this.infect(constraint, new SemanticAssertion[] { error2 });
		
		constraint = sem_mutation.get_assertions().greater_tn(loperand, rvalue);
		this.infect(constraint, new SemanticAssertion[] { error3 });
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
	private void arith_add_to_greater_eq(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, 
			Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_add, loperand, roperand, COperator.greater_eq);
		}
		else if(lvalue != null) {
			this.arith_add_to_greater_eq_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_add_to_greater_eq_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
		}
	}
	private void arith_add_to_greater_eq_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error2, error3;
		error2 = sem_mutation.get_assertions().set_value(expression, 0);
		error3 = sem_mutation.get_assertions().set_value(expression, 1);
		
		constraint = sem_mutation.get_assertions().smaller_tn(roperand, lvalue);
		this.infect(constraint, new SemanticAssertion[] { error3 });
		
		constraint = sem_mutation.get_assertions().equal_with(roperand, lvalue);
		this.infect(constraint, new SemanticAssertion[] { error3 });
		
		constraint = sem_mutation.get_assertions().greater_tn(roperand, lvalue);
		this.infect(constraint, new SemanticAssertion[] { error2 });
	}
	private void arith_add_to_greater_eq_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion constraint, error2, error3;
		error2 = sem_mutation.get_assertions().set_value(expression, 0);
		error3 = sem_mutation.get_assertions().set_value(expression, 1);
		
		constraint = sem_mutation.get_assertions().smaller_tn(loperand, rvalue);
		this.infect(constraint, new SemanticAssertion[] { error2 });
		
		constraint = sem_mutation.get_assertions().equal_with(loperand, rvalue);
		this.infect(constraint, new SemanticAssertion[] { error3 });
		
		constraint = sem_mutation.get_assertions().greater_tn(loperand, rvalue);
		this.infect(constraint, new SemanticAssertion[] { error3 });
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
	private void arith_add_to_smaller_tn(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, 
			Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_add, loperand, roperand, COperator.smaller_tn);
		}
		else if(lvalue != null) {
			this.arith_add_to_smaller_tn_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_add_to_smaller_tn_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void arith_add_to_smaller_tn_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error2, error3;
		error2 = sem_mutation.get_assertions().set_value(expression, 0);
		error3 = sem_mutation.get_assertions().set_value(expression, 1);
		
		constraint = sem_mutation.get_assertions().smaller_tn(roperand, lvalue);
		this.infect(constraint, new SemanticAssertion[] { error2 });
		
		constraint = sem_mutation.get_assertions().equal_with(roperand, lvalue);
		this.infect(constraint, new SemanticAssertion[] { error2 });
		
		constraint = sem_mutation.get_assertions().greater_tn(roperand, lvalue);
		this.infect(constraint, new SemanticAssertion[] { error3 });
	}
	private void arith_add_to_smaller_tn_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion constraint, error2, error3;
		error2 = sem_mutation.get_assertions().set_value(expression, 0);
		error3 = sem_mutation.get_assertions().set_value(expression, 1);
		
		constraint = sem_mutation.get_assertions().smaller_tn(loperand, rvalue);
		this.infect(constraint, new SemanticAssertion[] { error3 });
		
		constraint = sem_mutation.get_assertions().equal_with(loperand, rvalue);
		this.infect(constraint, new SemanticAssertion[] { error2 });
		
		constraint = sem_mutation.get_assertions().greater_tn(loperand, rvalue);
		this.infect(constraint, new SemanticAssertion[] { error2 });
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
	private void arith_add_to_smaller_eq(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, 
			Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_add, loperand, roperand, COperator.smaller_eq);
		}
		else if(lvalue != null) {
			this.arith_add_to_smaller_eq_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_add_to_smaller_eq_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void arith_add_to_smaller_eq_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error2, error3;
		error2 = sem_mutation.get_assertions().set_value(expression, 0);
		error3 = sem_mutation.get_assertions().set_value(expression, 1);
		
		constraint = sem_mutation.get_assertions().smaller_tn(roperand, lvalue);
		this.infect(constraint, new SemanticAssertion[] { error2 });
		
		constraint = sem_mutation.get_assertions().equal_with(roperand, lvalue);
		this.infect(constraint, new SemanticAssertion[] { error3 });
		
		constraint = sem_mutation.get_assertions().greater_tn(roperand, lvalue);
		this.infect(constraint, new SemanticAssertion[] { error3 });
	}
	private void arith_add_to_smaller_eq_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion constraint, error2, error3;
		error2 = sem_mutation.get_assertions().set_value(expression, 0);
		error3 = sem_mutation.get_assertions().set_value(expression, 1);
		
		constraint = sem_mutation.get_assertions().smaller_tn(loperand, rvalue);
		this.infect(constraint, new SemanticAssertion[] { error3 });
		
		constraint = sem_mutation.get_assertions().equal_with(loperand, rvalue);
		this.infect(constraint, new SemanticAssertion[] { error3 });
		
		constraint = sem_mutation.get_assertions().greater_tn(loperand, rvalue);
		this.infect(constraint, new SemanticAssertion[] { error2 });
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
	private void arith_add_to_equal_with(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, 
			Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_add, loperand, roperand, COperator.smaller_eq);
		}
		else if(lvalue != null) {
			this.arith_add_to_equal_with_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_add_to_equal_with_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
		}
	}
	private void arith_add_to_equal_with_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error2, error3;
		error2 = sem_mutation.get_assertions().set_value(expression, 0);
		error3 = sem_mutation.get_assertions().set_value(expression, 1);
		
		constraint = sem_mutation.get_assertions().smaller_tn(roperand, lvalue);
		this.infect(constraint, new SemanticAssertion[] { error2 });
		
		constraint = sem_mutation.get_assertions().equal_with(roperand, lvalue);
		this.infect(constraint, new SemanticAssertion[] { error3 });
		
		constraint = sem_mutation.get_assertions().greater_tn(roperand, lvalue);
		this.infect(constraint, new SemanticAssertion[] { error2 });
	}
	private void arith_add_to_equal_with_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion constraint, error2, error3;
		error2 = sem_mutation.get_assertions().set_value(expression, 0);
		error3 = sem_mutation.get_assertions().set_value(expression, 1);
		
		constraint = sem_mutation.get_assertions().smaller_tn(loperand, rvalue);
		this.infect(constraint, new SemanticAssertion[] { error2 });
		
		constraint = sem_mutation.get_assertions().equal_with(loperand, rvalue);
		this.infect(constraint, new SemanticAssertion[] { error3 });
		
		constraint = sem_mutation.get_assertions().greater_tn(loperand, rvalue);
		this.infect(constraint, new SemanticAssertion[] { error2 });
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
	private void arith_add_to_not_equals(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, 
			Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					arith_add, loperand, roperand, COperator.not_equals);
		}
		else if(lvalue != null) {
			this.arith_add_to_not_equals_cv(expression, lvalue, roperand);
		}
		else if(rvalue != null) {
			this.arith_add_to_not_equals_vc(expression, loperand, rvalue);
		}
		else {
			SemanticAssertion constraint, error2, error3;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void arith_add_to_not_equals_cv(CirExpression expression, Object lvalue, CirExpression roperand) throws Exception {
		SemanticAssertion constraint, error2, error3;
		error2 = sem_mutation.get_assertions().set_value(expression, 0);
		error3 = sem_mutation.get_assertions().set_value(expression, 1);
		
		constraint = sem_mutation.get_assertions().smaller_tn(roperand, lvalue);
		this.infect(constraint, new SemanticAssertion[] { error3 });
		
		constraint = sem_mutation.get_assertions().equal_with(roperand, lvalue);
		this.infect(constraint, new SemanticAssertion[] { error2 });
		
		constraint = sem_mutation.get_assertions().greater_tn(roperand, lvalue);
		this.infect(constraint, new SemanticAssertion[] { error3 });
	}
	private void arith_add_to_not_equals_vc(CirExpression expression, CirExpression loperand, Object rvalue) throws Exception {
		SemanticAssertion constraint, error2, error3;
		error2 = sem_mutation.get_assertions().set_value(expression, 0);
		error3 = sem_mutation.get_assertions().set_value(expression, 1);
		
		constraint = sem_mutation.get_assertions().smaller_tn(loperand, rvalue);
		this.infect(constraint, new SemanticAssertion[] { error3 });
		
		constraint = sem_mutation.get_assertions().equal_with(loperand, rvalue);
		this.infect(constraint, new SemanticAssertion[] { error2 });
		
		constraint = sem_mutation.get_assertions().greater_tn(loperand, rvalue);
		this.infect(constraint, new SemanticAssertion[] { error3 });
	}
	
}
