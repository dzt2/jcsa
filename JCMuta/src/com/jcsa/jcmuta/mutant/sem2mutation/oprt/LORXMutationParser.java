package com.jcsa.jcmuta.mutant.sem2mutation.oprt;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.sem2mutation.muta.SemanticAssertion;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.COperator;

public class LORXMutationParser extends OXXNMutationParser {

	@Override
	protected void generate_infections(AstMutation ast_mutation, CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		switch(ast_mutation.get_mutation_operator()) {
		case logic_ior_to_arith_add: this.logic_ior_to_arith_add(expression, loperand, roperand, lvalue, rvalue); break;
		case logic_ior_to_arith_sub: this.logic_ior_to_arith_sub(expression, loperand, roperand, lvalue, rvalue); break;
		case logic_ior_to_arith_mul: this.logic_ior_to_arith_mul(expression, loperand, roperand, lvalue, rvalue); break;
		case logic_ior_to_arith_div: this.logic_ior_to_arith_div(expression, loperand, roperand, lvalue, rvalue); break;
		case logic_ior_to_arith_mod: this.logic_ior_to_arith_mod(expression, loperand, roperand, lvalue, rvalue); break;
		case logic_ior_to_bitws_and: this.logic_ior_to_bitws_and(expression, loperand, roperand, lvalue, rvalue); break;
		case logic_ior_to_bitws_ior: this.logic_ior_to_bitws_ior(expression, loperand, roperand, lvalue, rvalue); break;
		case logic_ior_to_bitws_xor: this.logic_ior_to_bitws_xor(expression, loperand, roperand, lvalue, rvalue); break;
		case logic_ior_to_bitws_lsh: this.logic_ior_to_bitws_lsh(expression, loperand, roperand, lvalue, rvalue); break;
		case logic_ior_to_bitws_rsh: this.logic_ior_to_bitws_rsh(expression, loperand, roperand, lvalue, rvalue); break;
		case logic_ior_to_logic_and: this.logic_ior_to_logic_and(expression, loperand, roperand, lvalue, rvalue); break;
		case logic_ior_to_greater_tn:this.logic_ior_to_greater_tn(expression, loperand, roperand, lvalue, rvalue);break;
		case logic_ior_to_greater_eq:this.logic_ior_to_greater_eq(expression, loperand, roperand, lvalue, rvalue);break;
		case logic_ior_to_smaller_tn:this.logic_ior_to_smaller_tn(expression, loperand, roperand, lvalue, rvalue);break;
		case logic_ior_to_smaller_eq:this.logic_ior_to_smaller_eq(expression, loperand, roperand, lvalue, rvalue);break;
		case logic_ior_to_equal_with:this.logic_ior_to_equal_with(expression, loperand, roperand, lvalue, rvalue);break;
		case logic_ior_to_not_equals:this.logic_ior_to_not_equals(expression, loperand, roperand, lvalue, rvalue);break;
		default: throw new IllegalArgumentException("Invalid mutation operator");
		}
	}
	
	/* implementation */
	private void logic_ior_to_arith_add(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		/** equivalent mutant **/	return;
	}
	private void logic_ior_to_arith_sub(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.logic_or, loperand, roperand, COperator.arith_sub);
		}
		else {
			SemanticAssertion error3;
			error3 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint1, constraint2;
			constraint1 = this.bool_verification(loperand, true);
			constraint2 = this.bool_verification(roperand, true);
			this.infect(new SemanticAssertion[] { constraint1, constraint2 }, 
					new SemanticAssertion[] { error3 });
		}
	}
	private void logic_ior_to_arith_mul(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.logic_or, loperand, roperand, COperator.arith_mul);
		}
		else {
			SemanticAssertion error3;
			error3 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint1, constraint2;
			constraint1 = this.bool_verification(loperand, true);
			constraint2 = this.bool_verification(roperand, false);
			this.infect(new SemanticAssertion[] { constraint1, constraint2 }, 
					new SemanticAssertion[] { error3 });
			
			constraint1 = this.bool_verification(loperand, false);
			constraint2 = this.bool_verification(roperand, true);
			this.infect(new SemanticAssertion[] { constraint1, constraint2 }, 
					new SemanticAssertion[] { error3 });
		}
	}
	private void logic_ior_to_arith_div(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.logic_or, loperand, roperand, COperator.arith_div);
		}
		else {
			SemanticAssertion error3;
			error3 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint1, constraint2;
			constraint1 = this.bool_verification(roperand, false);
			this.infect(constraint1, sem_mutation.get_assertions().trapping());
			
			constraint1 = this.bool_verification(loperand, false);
			constraint2 = this.bool_verification(roperand, true);
			this.infect(new SemanticAssertion[] { constraint1, constraint2 }, 
					new SemanticAssertion[] { error3 });
		}
	}
	private void logic_ior_to_arith_mod(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.logic_or, loperand, roperand, COperator.arith_mod);
		}
		else {
			SemanticAssertion error3;
			error3 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint;
			constraint = this.bool_verification(roperand, false);
			this.infect(constraint, sem_mutation.get_assertions().trapping());
			
			constraint = this.bool_verification(roperand, true);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void logic_ior_to_bitws_and(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.logic_or, loperand, roperand, COperator.bit_and);
		}
		else {
			SemanticAssertion error3;
			error3 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint1, constraint2;
			constraint1 = this.bool_verification(loperand, true);
			constraint2 = this.bool_verification(roperand, false);
			this.infect(new SemanticAssertion[] { constraint1, constraint2 }, 
					new SemanticAssertion[] { error3 });
			
			constraint1 = this.bool_verification(loperand, false);
			constraint2 = this.bool_verification(roperand, true);
			this.infect(new SemanticAssertion[] { constraint1, constraint2 }, 
					new SemanticAssertion[] { error3 });
		}
	}
	private void logic_ior_to_bitws_ior(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		/** equivalent mutation **/
	}
	private void logic_ior_to_bitws_xor(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.logic_or, loperand, roperand, COperator.bit_xor);
		}
		else {
			SemanticAssertion error3;
			error3 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint1, constraint2;
			constraint1 = this.bool_verification(loperand, true);
			constraint2 = this.bool_verification(roperand, true);
			this.infect(new SemanticAssertion[] { constraint1, constraint2 }, 
					new SemanticAssertion[] { error3 });
		}
	}
	private void logic_ior_to_bitws_lsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.logic_or, loperand, roperand, COperator.left_shift);
		}
		else {
			SemanticAssertion error3;
			error3 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint1, constraint2;
			constraint1 = this.bool_verification(loperand, false);
			constraint2 = this.bool_verification(roperand, true);
			this.infect(new SemanticAssertion[] { constraint1, constraint2 }, 
					new SemanticAssertion[] { error3 });
		}
	}
	private void logic_ior_to_bitws_rsh(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.logic_or, loperand, roperand, COperator.righ_shift);
		}
		else {
			SemanticAssertion error3;
			error3 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint = this.bool_verification(roperand, true);
			this.infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void logic_ior_to_logic_and(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.logic_or, loperand, roperand, COperator.logic_and);
		}
		else {
			SemanticAssertion error3;
			error3 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint1, constraint2;
			constraint1 = this.bool_verification(loperand, true);
			constraint2 = this.bool_verification(roperand, false);
			this.infect(new SemanticAssertion[] { constraint1, constraint2 }, 
					new SemanticAssertion[] { error3 });
			
			constraint1 = this.bool_verification(loperand, false);
			constraint2 = this.bool_verification(roperand, true);
			this.infect(new SemanticAssertion[] { constraint1, constraint2 }, 
					new SemanticAssertion[] { error3 });
		}
	}
	private void logic_ior_to_greater_tn(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.logic_or, loperand, roperand, COperator.greater_tn);
		}
		else {
			SemanticAssertion error3;
			error3 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint = this.bool_verification(roperand, true);
			infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void logic_ior_to_greater_eq(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.logic_or, loperand, roperand, COperator.greater_eq);
		}
		else {
			SemanticAssertion error2, error3, error4;
			error2 = sem_mutation.get_assertions().not_value(expression);
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint1, constraint2;
			constraint1 = this.bool_verification(loperand, false);
			this.infect(constraint1, new SemanticAssertion[] { error2 });
			
			constraint1 = this.bool_verification(loperand, false);
			constraint2 = this.bool_verification(roperand, false);
			this.infect(
					new SemanticAssertion[] { constraint1, constraint2 }, 
					new SemanticAssertion[] { error3 });
			
			constraint1 = this.bool_verification(loperand, false);
			constraint2 = this.bool_verification(roperand, true);
			this.infect(
					new SemanticAssertion[] { constraint1, constraint2 }, 
					new SemanticAssertion[] { error4 });
		}
	}
	private void logic_ior_to_smaller_tn(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.logic_or, loperand, roperand, COperator.smaller_tn);
		}
		else {
			SemanticAssertion error3;
			error3 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint = this.bool_verification(loperand, true);
			infect(constraint, new SemanticAssertion[] { error3 });
		}
	}
	private void logic_ior_to_smaller_eq(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.logic_or, loperand, roperand, COperator.smaller_eq);
		}
		else {
			SemanticAssertion error2, error3, error4;
			error2 = sem_mutation.get_assertions().not_value(expression);
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint1, constraint2;
			constraint1 = this.bool_verification(roperand, false);
			this.infect(constraint1, new SemanticAssertion[] { error2 });
			
			constraint1 = this.bool_verification(loperand, false);
			constraint2 = this.bool_verification(roperand, false);
			this.infect(
					new SemanticAssertion[] { constraint1, constraint2 }, 
					new SemanticAssertion[] { error3 });
			
			constraint1 = this.bool_verification(loperand, true);
			constraint2 = this.bool_verification(roperand, false);
			this.infect(
					new SemanticAssertion[] { constraint1, constraint2 }, 
					new SemanticAssertion[] { error4 });
		}
	}
	private void logic_ior_to_equal_with(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.logic_or, loperand, roperand, COperator.equal_with);
		}
		else {
			SemanticAssertion error3, error4;
			error3 = sem_mutation.get_assertions().set_value(expression, true);
			error4 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint1, constraint2;
			constraint1 = this.bool_verification(loperand, false);
			constraint2 = this.bool_verification(roperand, false);
			this.infect(
					new SemanticAssertion[] { constraint1, constraint2 }, 
					new SemanticAssertion[] { error3 });
			
			constraint1 = this.bool_verification(loperand, true);
			constraint2 = this.bool_verification(roperand, false);
			this.infect(
					new SemanticAssertion[] { constraint1, constraint2 }, 
					new SemanticAssertion[] { error4 });
			
			constraint1 = this.bool_verification(loperand, false);
			constraint2 = this.bool_verification(roperand, true);
			this.infect(
					new SemanticAssertion[] { constraint1, constraint2 }, 
					new SemanticAssertion[] { error4 });
		}
	}
	private void logic_ior_to_not_equals(CirExpression expression, CirExpression loperand,
			CirExpression roperand, Object lvalue, Object rvalue) throws Exception {
		if(lvalue != null && rvalue != null) {
			this.decide_cons_to_cons(expression, 
					COperator.logic_or, loperand, roperand, COperator.not_equals);
		}
		else {
			SemanticAssertion error3;
			error3 = sem_mutation.get_assertions().set_value(expression, false);
			
			SemanticAssertion constraint1, constraint2;
			constraint1 = this.bool_verification(loperand, true);
			constraint2 = this.bool_verification(roperand, true);
			this.infect(
					new SemanticAssertion[] { constraint1, constraint2 }, 
					new SemanticAssertion[] { error3 });
		}
	}
}
