package com.jcsa.jcmuta.mutant.sem2mutation.oprt;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class SMTXMutationParser extends OXXNMutationParser {

	@Override
	protected void generate_infections(AstMutation ast_mutation, CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		switch(ast_mutation.get_mutation_operator()) {
		case smaller_tn_to_arith_add: this.smaller_tn_to_arith_add(expression, loperand, roperand, lvalue, rvalue); break;
		case smaller_tn_to_arith_sub: this.smaller_tn_to_arith_sub(expression, loperand, roperand, lvalue, rvalue); break;
		case smaller_tn_to_arith_mul: this.smaller_tn_to_arith_mul(expression, loperand, roperand, lvalue, rvalue); break;
		case smaller_tn_to_arith_div: this.smaller_tn_to_arith_div(expression, loperand, roperand, lvalue, rvalue); break;
		case smaller_tn_to_arith_mod: this.smaller_tn_to_arith_mod(expression, loperand, roperand, lvalue, rvalue); break;
		case smaller_tn_to_bitws_and: this.smaller_tn_to_bitws_and(expression, loperand, roperand, lvalue, rvalue); break;
		case smaller_tn_to_bitws_ior: this.smaller_tn_to_bitws_ior(expression, loperand, roperand, lvalue, rvalue); break;
		case smaller_tn_to_bitws_xor: this.smaller_tn_to_bitws_xor(expression, loperand, roperand, lvalue, rvalue); break;
		case smaller_tn_to_bitws_lsh: this.smaller_tn_to_bitws_lsh(expression, loperand, roperand, lvalue, rvalue); break;
		case smaller_tn_to_bitws_rsh: this.smaller_tn_to_bitws_rsh(expression, loperand, roperand, lvalue, rvalue); break;
		case smaller_tn_to_logic_and: this.smaller_tn_to_logic_and(expression, loperand, roperand, lvalue, rvalue); break;
		case smaller_tn_to_logic_ior: this.smaller_tn_to_logic_ior(expression, loperand, roperand, lvalue, rvalue); break;
		case smaller_tn_to_smaller_eq:this.smaller_tn_to_smaller_eq(expression, loperand, roperand, lvalue, rvalue);break;
		case smaller_tn_to_greater_tn:this.smaller_tn_to_greater_tn(expression, loperand, roperand, lvalue, rvalue);break;
		case smaller_tn_to_greater_eq:this.smaller_tn_to_greater_eq(expression, loperand, roperand, lvalue, rvalue);break;
		case smaller_tn_to_equal_with:this.smaller_tn_to_equal_with(expression, loperand, roperand, lvalue, rvalue);break;
		case smaller_tn_to_not_equals:this.smaller_tn_to_not_equals(expression, loperand, roperand, lvalue, rvalue);break;
		default: throw new IllegalArgumentException("Invalid mutation operator");
		}
	}
	
	/* implementation */
	private void smaller_tn_to_arith_add(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.smaller_tn, loperand, roperand, COperator.arith_add);
		}
		else {
			SemanticAssertion constraint, error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().is_negative(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void smaller_tn_to_arith_sub(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.smaller_tn, loperand, roperand, COperator.arith_sub);
		}
		else {
			SemanticAssertion constraint, error3;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void smaller_tn_to_arith_mul(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.smaller_tn, loperand, roperand, COperator.arith_mul);
		}
		else {
			SemanticAssertion constraint, error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void smaller_tn_to_arith_div(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.smaller_tn, loperand, roperand, COperator.arith_div);
		}
		else {
			SemanticAssertion constraint, error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, sem_mutation.get_assertions().trapping());
		}
	}
	private void smaller_tn_to_arith_mod(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.smaller_tn, loperand, roperand, COperator.arith_mod);
		}
		else {
			SemanticAssertion constraint, error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().is_multiply(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, sem_mutation.get_assertions().trapping());
		}
	}
	private void smaller_tn_to_bitws_and(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.smaller_tn, loperand, roperand, COperator.bit_and);
		}
		else {
			SemanticAssertion constraint, error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void smaller_tn_to_bitws_ior(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.smaller_tn, loperand, roperand, COperator.bit_or);
		}
		else {
			SemanticAssertion constraint, error3;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void smaller_tn_to_bitws_xor(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.smaller_tn, loperand, roperand, COperator.bit_xor);
		}
		else {
			SemanticAssertion constraint, error3;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void smaller_tn_to_bitws_lsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.smaller_tn, loperand, roperand, COperator.left_shift);
		}
		else {
			SemanticAssertion constraint, error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void smaller_tn_to_bitws_rsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.smaller_tn, loperand, roperand, COperator.righ_shift);
		}
		else {
			SemanticAssertion constraint, error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().greater_tn(roperand, max_shifting);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void smaller_tn_to_logic_and(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.smaller_tn, loperand, roperand, COperator.logic_and);
		}
		else {
			SemanticAssertion constraint, error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
			
			constraint = sem_mutation.get_assertions().equal_with(roperand, 0);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void smaller_tn_to_logic_ior(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.smaller_tn, loperand, roperand, COperator.logic_or);
		}
		else {
			SemanticAssertion constraint, error3;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void smaller_tn_to_greater_eq(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.smaller_tn, loperand, roperand, COperator.greater_eq);
		}
		else {
			SemanticAssertion constraint, error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			this.infect(sem_mutation.get_assertions().not_value(expression));
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void smaller_tn_to_greater_tn(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.smaller_tn, loperand, roperand, COperator.greater_tn);
		}
		else {
			SemanticAssertion constraint, error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void smaller_tn_to_smaller_eq(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.smaller_tn, loperand, roperand, COperator.smaller_eq);
		}
		else {
			SemanticAssertion error3;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			
			SemanticAssertion constraint;
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void smaller_tn_to_equal_with(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.smaller_tn, loperand, roperand, COperator.equal_with);
		}
		else {
			SemanticAssertion constraint, error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			constraint = sem_mutation.get_assertions().equal_with(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
			
			constraint = sem_mutation.get_assertions().smaller_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error4 });
		}
	}
	private void smaller_tn_to_not_equals(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.smaller_tn, loperand, roperand, COperator.not_equals);
		}
		else {
			SemanticAssertion constraint, error3;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			
			constraint = sem_mutation.get_assertions().greater_tn(loperand, roperand);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	
}
