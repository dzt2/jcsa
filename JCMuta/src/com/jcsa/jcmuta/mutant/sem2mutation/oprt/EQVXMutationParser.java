package com.jcsa.jcmuta.mutant.sem2mutation.oprt;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class EQVXMutationParser extends OXXNMutationParser {

	@Override
	protected void generate_infections(AstMutation ast_mutation, CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		switch(ast_mutation.get_mutation_operator()) {
		case equal_with_to_arith_add: this.equal_with_to_arith_add(expression, loperand, roperand, lvalue, rvalue); break;
		case equal_with_to_arith_sub: this.equal_with_to_arith_sub(expression, loperand, roperand, lvalue, rvalue); break;
		case equal_with_to_arith_mul: this.equal_with_to_arith_mul(expression, loperand, roperand, lvalue, rvalue); break;
		case equal_with_to_arith_div: this.equal_with_to_arith_div(expression, loperand, roperand, lvalue, rvalue); break;
		case equal_with_to_arith_mod: this.equal_with_to_arith_mod(expression, loperand, roperand, lvalue, rvalue); break;
		case equal_with_to_bitws_and: this.equal_with_to_bitws_and(expression, loperand, roperand, lvalue, rvalue); break;
		case equal_with_to_bitws_ior: this.equal_with_to_bitws_ior(expression, loperand, roperand, lvalue, rvalue); break;
		case equal_with_to_bitws_xor: this.equal_with_to_bitws_xor(expression, loperand, roperand, lvalue, rvalue); break;
		case equal_with_to_bitws_lsh: this.equal_with_to_bitws_lsh(expression, loperand, roperand, lvalue, rvalue); break;
		case equal_with_to_bitws_rsh: this.equal_with_to_bitws_rsh(expression, loperand, roperand, lvalue, rvalue); break;
		case equal_with_to_logic_and: this.equal_with_to_logic_and(expression, loperand, roperand, lvalue, rvalue); break;
		case equal_with_to_logic_ior: this.equal_with_to_logic_ior(expression, loperand, roperand, lvalue, rvalue); break;
		
		case equal_with_to_greater_tn: this.equal_with_to_greater_tn(expression, loperand, roperand, lvalue, rvalue); break;
		case equal_with_to_smaller_tn: this.equal_with_to_smaller_tn(expression, loperand, roperand, lvalue, rvalue); break;
		case equal_with_to_greater_eq: this.equal_with_to_greater_eq(expression, loperand, roperand, lvalue, rvalue); break;
		case equal_with_to_smaller_eq: this.equal_with_to_smaller_eq(expression, loperand, roperand, lvalue, rvalue); break;
		case equal_with_to_not_equals: this.equal_with_to_not_equals(expression, loperand, roperand, lvalue, rvalue); break;
		default: throw new IllegalArgumentException("Invalid mutation operator.");
		}
	}
	
	/* implementation */
	private void equal_with_to_arith_add(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.equal_with, loperand, roperand, COperator.arith_add);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().not_equals(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().is_negative(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void equal_with_to_arith_sub(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.equal_with, loperand, roperand, COperator.arith_sub);
		}
		else {
			SemanticAssertion error2, error3, error4;
			error2 = sem_mutation.get_assertions().not_value(expression);
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			this.infect(new SemanticAssertion[] { error2 });
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().not_equals(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void equal_with_to_arith_mul(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.equal_with, loperand, roperand, COperator.arith_mul);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().not_equals(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void equal_with_to_arith_div(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.equal_with, loperand, roperand, COperator.arith_div);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().not_equals(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, sem_mutation.get_assertions().trapping());
		}
	}
	private void equal_with_to_arith_mod(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.equal_with, loperand, roperand, COperator.arith_mod);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().not_equals(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().is_multiply(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, sem_mutation.get_assertions().trapping());
		}
	}
	private void equal_with_to_bitws_and(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.equal_with, loperand, roperand, COperator.bit_and);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().not_equals(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void equal_with_to_bitws_ior(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.equal_with, loperand, roperand, COperator.bit_or);
		}
		else {
			SemanticAssertion error3;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().not_equals(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void equal_with_to_bitws_xor(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.equal_with, loperand, roperand, COperator.bit_xor);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			this.infect(new SemanticAssertion[] { sem_mutation.get_assertions().not_value(expression) });
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().not_equals(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void equal_with_to_bitws_lsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.equal_with, loperand, roperand, COperator.left_shift);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().not_equals(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void equal_with_to_bitws_rsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.equal_with, loperand, roperand, COperator.righ_shift);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().not_equals(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void equal_with_to_logic_and(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.equal_with, loperand, roperand, COperator.logic_and);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().not_equals(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void equal_with_to_logic_ior(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.equal_with, loperand, roperand, COperator.logic_or);
		}
		else {
			SemanticAssertion error3;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().not_equals(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void equal_with_to_greater_tn(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.equal_with, loperand, roperand, COperator.greater_tn);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void equal_with_to_smaller_tn(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.equal_with, loperand, roperand, COperator.smaller_tn);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void equal_with_to_greater_eq(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.equal_with, loperand, roperand, COperator.greater_eq);
		}
		else {
			SemanticAssertion error3;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void equal_with_to_smaller_eq(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.equal_with, loperand, roperand, COperator.greater_eq);
		}
		else {
			SemanticAssertion error3;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void equal_with_to_not_equals(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.equal_with, loperand, roperand, COperator.not_equals);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			this.infect(new SemanticAssertion[] { sem_mutation.get_assertions().not_value(expression) });
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().not_equals(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	
}
