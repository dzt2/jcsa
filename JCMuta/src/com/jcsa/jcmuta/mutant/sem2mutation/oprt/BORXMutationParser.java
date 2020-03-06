package com.jcsa.jcmuta.mutant.sem2mutation.oprt;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class BORXMutationParser extends OXXNMutationParser {
	
	@Override
	protected void generate_infections(AstMutation ast_mutation, CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		switch(ast_mutation.get_mutation_operator()) {
		case bitws_ior_to_arith_add: this.bitws_ior_to_arith_add(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_to_arith_sub: this.bitws_ior_to_arith_sub(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_to_arith_mul: this.bitws_ior_to_arith_mul(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_to_arith_div: this.bitws_ior_to_arith_div(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_to_arith_mod: this.bitws_ior_to_arith_mod(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_to_bitws_and: this.bitws_ior_to_bitws_and(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_to_bitws_xor: this.bitws_ior_to_bitws_xor(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_to_bitws_lsh: this.bitws_ior_to_bitws_lsh(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_to_bitws_rsh: this.bitws_ior_to_bitws_rsh(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_to_logic_and: this.bitws_ior_to_logic_and(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_to_logic_ior: this.bitws_ior_to_logic_ior(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_to_greater_tn: this.bitws_ior_to_greater_tn(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_to_greater_eq: this.bitws_ior_to_greater_eq(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_to_smaller_tn: this.bitws_ior_to_smaller_tn(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_to_smaller_eq: this.bitws_ior_to_smaller_eq(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_to_equal_with: this.bitws_ior_to_equal_with(expression, loperand, roperand, lvalue, rvalue); break;
		case bitws_ior_to_not_equals: this.bitws_ior_to_not_equals(expression, loperand, roperand, lvalue, rvalue); break;
		default: throw new IllegalArgumentException("Invalid mutation operator");
		}
	}
	
	/* implementation method */
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
	private void bitws_ior_to_logic_and(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_or, loperand, roperand, COperator.logic_and);
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
			this.infect(constraint, new SemanticAssertion[] { error2, error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error2, error3 });
		}
	}
	private void bitws_ior_to_logic_ior(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_or, loperand, roperand, COperator.logic_or);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 1);
			error3 = sem_mutation.get_assertions().inc_value(expression);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			
			constraint = sem_mutation.get_assertions().not_equals(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().not_equals(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error2, error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			this.infect(constraint, new SemanticAssertion[] { error2, error3 });
		}
	}
	private void bitws_ior_to_greater_tn(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_or, loperand, roperand, COperator.greater_tn);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			error4 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void bitws_ior_to_greater_eq(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_or, loperand, roperand, COperator.greater_eq);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			error4 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void bitws_ior_to_smaller_tn(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_or, loperand, roperand, COperator.smaller_tn);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			error4 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void bitws_ior_to_smaller_eq(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_or, loperand, roperand, COperator.smaller_eq);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			error4 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void bitws_ior_to_equal_with(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_or, loperand, roperand, COperator.equal_with);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			error4 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void bitws_ior_to_not_equals(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.compute_const_to_const(expression, COperator.
					bit_or, loperand, roperand, COperator.not_equals);
		}
		else {
			SemanticAssertion constraint, error2, error3, error4;
			error2 = sem_mutation.get_assertions().set_value(expression, 0);
			error3 = sem_mutation.get_assertions().set_value(expression, 1);
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error2 });
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().greater_tn(expression, 0);
			error4 = sem_mutation.get_assertions().dec_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(expression, 0);
			error4 = sem_mutation.get_assertions().inc_value(expression);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	
}
