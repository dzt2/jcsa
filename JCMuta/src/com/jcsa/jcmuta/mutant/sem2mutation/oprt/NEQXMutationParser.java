package com.jcsa.jcmuta.mutant.sem2mutation.oprt;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class NEQXMutationParser extends OXXNMutationParser {

	@Override
	protected void generate_infections(AstMutation ast_mutation, CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		switch(ast_mutation.get_mutation_operator()) {
		case not_equals_to_arith_add: this.not_equals_to_arith_add(expression, loperand, roperand, lvalue, rvalue); break;
		case not_equals_to_arith_sub: this.not_equals_to_arith_sub(expression, loperand, roperand, lvalue, rvalue); break;
		case not_equals_to_arith_mul: this.not_equals_to_arith_mul(expression, loperand, roperand, lvalue, rvalue); break;
		case not_equals_to_arith_div: this.not_equals_to_arith_div(expression, loperand, roperand, lvalue, rvalue); break;
		case not_equals_to_arith_mod: this.not_equals_to_arith_mod(expression, loperand, roperand, lvalue, rvalue); break;
		case not_equals_to_bitws_and: this.not_equals_to_bitws_and(expression, loperand, roperand, lvalue, rvalue); break;
		case not_equals_to_bitws_ior: this.not_equals_to_bitws_ior(expression, loperand, roperand, lvalue, rvalue); break;
		case not_equals_to_bitws_xor: this.not_equals_to_bitws_xor(expression, loperand, roperand, lvalue, rvalue); break;
		case not_equals_to_bitws_lsh: this.not_equals_to_bitws_lsh(expression, loperand, roperand, lvalue, rvalue); break;
		case not_equals_to_bitws_rsh: this.not_equals_to_bitws_rsh(expression, loperand, roperand, lvalue, rvalue); break;
		case not_equals_to_logic_and: this.not_equals_to_logic_and(expression, loperand, roperand, lvalue, rvalue); break;
		case not_equals_to_logic_ior: this.not_equals_to_logic_ior(expression, loperand, roperand, lvalue, rvalue); break;
		case not_equals_to_greater_tn:this.not_equals_to_greater_tn(expression, loperand, roperand, lvalue, rvalue);break;
		case not_equals_to_greater_eq:this.not_equals_to_greater_eq(expression, loperand, roperand, lvalue, rvalue);break;
		case not_equals_to_smaller_tn:this.not_equals_to_smaller_tn(expression, loperand, roperand, lvalue, rvalue);break;
		case not_equals_to_smaller_eq:this.not_equals_to_smaller_eq(expression, loperand, roperand, lvalue, rvalue);break;
		case not_equals_to_equal_with:this.not_equals_to_equal_with(expression, loperand, roperand, lvalue, rvalue);break;
		default: throw new IllegalArgumentException("Invalid mutation operator.");
		}
	}
	
	/* implementation */
	private void not_equals_to_arith_add(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.not_equals, loperand, roperand, COperator.arith_add);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().is_negative(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void not_equals_to_arith_sub(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		/** equivalent mutation **/
	}
	private void not_equals_to_arith_mul(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.not_equals, loperand, roperand, COperator.arith_mul);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void not_equals_to_arith_div(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.not_equals, loperand, roperand, COperator.arith_div);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, sem_mutation.get_assertions().trapping());
		}
	}
	private void not_equals_to_arith_mod(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.not_equals, loperand, roperand, COperator.arith_mod);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().is_multiply(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, sem_mutation.get_assertions().trapping());
		}
	}
	private void not_equals_to_bitws_and(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.not_equals, loperand, roperand, COperator.bit_and);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void not_equals_to_bitws_ior(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.not_equals, loperand, roperand, COperator.bit_or);
		}
		else {
			SemanticAssertion error3;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void not_equals_to_bitws_xor(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		/** equivalent mutation **/
	}
	private void not_equals_to_bitws_lsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.not_equals, loperand, roperand, COperator.left_shift);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void not_equals_to_bitws_rsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.not_equals, loperand, roperand, COperator.righ_shift);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void not_equals_to_logic_and(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.not_equals, loperand, roperand, COperator.logic_and);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void not_equals_to_logic_ior(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.not_equals, loperand, roperand, COperator.logic_or);
		}
		else {
			SemanticAssertion error3;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void not_equals_to_greater_tn(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.not_equals, loperand, roperand, COperator.greater_tn);
		}
		else {
			SemanticAssertion error4;
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void not_equals_to_smaller_tn(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.not_equals, loperand, roperand, COperator.smaller_tn);
		}
		else {
			SemanticAssertion error4;
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void not_equals_to_greater_eq(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.not_equals, loperand, roperand, COperator.greater_tn);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void not_equals_to_smaller_eq(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.not_equals, loperand, roperand, COperator.greater_tn);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void not_equals_to_equal_with(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.not_equals, loperand, roperand, COperator.greater_tn);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			this.infect(sem_mutation.get_assertions().not_value(expression));
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	
}
