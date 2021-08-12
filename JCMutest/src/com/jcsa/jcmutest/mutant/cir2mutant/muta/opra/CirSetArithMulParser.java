package com.jcsa.jcmutest.mutant.cir2mutant.muta.opra;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirOperatorParser;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class CirSetArithMulParser extends CirOperatorParser {

	@Override
	protected boolean to_assign() throws Exception {
		/** declarations **/
		List<CirAttribute> constraints = new ArrayList<CirAttribute>();
		CirAttribute constraint, init_error; SymbolExpression condition;
		
		/** constraint: {x = x * y --> x = y} : {x != 1 && y != 0} **/
		condition = SymbolFactory.not_equals(this.loperand, Integer.valueOf(1));
		constraints.add(this.get_constraint(condition));
		condition = SymbolFactory.not_equals(this.roperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		constraint = this.conjunct(constraints);
		
		/** determine initial error from the mutation **/
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.roperand);
		}
		
		/** update the constraint-based infection pair **/
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_add() throws Exception {
		/** declarations **/
		List<CirAttribute> constraints = new ArrayList<CirAttribute>();
		CirAttribute constraint, init_error; SymbolExpression condition;
		
		/** constraint: {x * y --> x + y} : {x != 0 || y != 0} **/
		condition = SymbolFactory.not_equals(this.loperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		condition = SymbolFactory.not_equals(this.roperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		constraint = this.disjunct(constraints);
		
		/** determine initial error from the mutation **/
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.arith_add, this.loperand, this.roperand));
		}
		
		/** update the constraint-based infection pair **/
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_sub() throws Exception {
		/** declarations **/
		List<CirAttribute> constraints = new ArrayList<CirAttribute>();
		CirAttribute constraint, init_error; SymbolExpression condition;
		
		/** constraint: {x * y --> x - y} : {x != 0 || y != 0} **/
		condition = SymbolFactory.not_equals(this.loperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		condition = SymbolFactory.not_equals(this.roperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		constraint = this.disjunct(constraints);
		
		/** determine initial error from the mutation **/
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.arith_sub, this.loperand, this.roperand));
		}
		
		/** update the constraint-based infection pair **/
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_mul() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean arith_div() throws Exception {
		/** declarations **/
		List<CirAttribute> constraints = new ArrayList<CirAttribute>();
		CirAttribute constraint, init_error; SymbolExpression condition;
		
		/** constraint: {x * y --> x / y} ==> (x != 0 && y != 1 && y != -1) **/
		condition = SymbolFactory.not_equals(this.loperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		condition = SymbolFactory.not_equals(this.roperand, Integer.valueOf(1));
		constraints.add(this.get_constraint(condition));
		condition = SymbolFactory.not_equals(this.roperand, Integer.valueOf(-1));
		constraints.add(this.get_constraint(condition));
		constraint = this.conjunct(constraints);
		
		/** determine initial error from the mutation **/
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.arith_div, this.loperand, this.roperand));
		}
		
		/** update the constraint-based infection pair **/
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_mod() throws Exception {
		/** declarations **/
		CirAttribute constraint, init_error; SymbolExpression condition;
		
		/** constraint: (x * y --> x % y) ==> (x != 0) **/
		condition = SymbolFactory.not_equals(this.loperand, Integer.valueOf(0));
		constraint = this.get_constraint(condition);
		
		/** determine initial error from the mutation **/
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.arith_mod, this.loperand, this.roperand));
		}
		
		/** update the constraint-based infection pair **/
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_and() throws Exception {
		/** declarations **/
		List<CirAttribute> constraints = new ArrayList<CirAttribute>();
		CirAttribute constraint, init_error; SymbolExpression condition;
		
		/** constraint: {x * y --> x & y} : {x != 0 && y != 0} **/
		condition = SymbolFactory.not_equals(this.loperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		condition = SymbolFactory.not_equals(this.roperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		constraint = this.conjunct(constraints);
		
		/** determine initial error from the mutation **/
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.bit_and, this.loperand, this.roperand));
		}
		
		/** update the constraint-based infection pair **/
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		/** declarations **/
		CirAttribute constraint, init_error; 
		
		/** constraint: (x * y --> x | y) ==> TRUE **/
		constraint = this.get_constraint(Boolean.TRUE);
		
		/** determine initial error from the mutation **/
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.bit_or, this.loperand, this.roperand));
		}
		
		/** update the constraint-based infection pair **/
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		/** declarations **/
		CirAttribute constraint, init_error; 
		
		/** constraint: (x * y --> x ^ y) ==> TRUE **/
		constraint = this.get_constraint(Boolean.TRUE);
		
		/** determine initial error from the mutation **/
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.bit_xor, this.loperand, this.roperand));
		}
		
		/** update the constraint-based infection pair **/
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		/** declarations **/
		CirAttribute constraint, init_error; 
		
		/** constraint: (x * y --> x << y) ==> x != 0 **/
		constraint = this.get_constraint(SymbolFactory.
				not_equals(this.loperand, Integer.valueOf(0)));
		
		/** determine initial error from the mutation **/
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.left_shift, this.loperand, this.roperand));
		}
		
		/** update the constraint-based infection pair **/
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		/** declarations **/
		CirAttribute constraint, init_error; 
		
		/** constraint: (x * y --> x >> y) ==> x != 0 **/
		constraint = this.get_constraint(SymbolFactory.
				not_equals(this.loperand, Integer.valueOf(0)));
		
		/** determine initial error from the mutation **/
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.righ_shift, this.loperand, this.roperand));
		}
		
		/** update the constraint-based infection pair **/
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean logic_and() throws Exception {
		/** declarations **/
		List<CirAttribute> constraints = new ArrayList<CirAttribute>();
		CirAttribute constraint, init_error; SymbolExpression condition;
		
		/** constraint: {x * y --> x && y} ==> (either x or y is non_boolean) **/
		condition = this.non_bool_condition(this.loperand);
		constraints.add(this.get_constraint(condition));
		condition = this.non_bool_condition(this.roperand);
		constraints.add(this.get_constraint(condition));
		constraint = this.disjunct(constraints);
		
		/** determine initial error from the mutation **/
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.logic_and, this.loperand, this.roperand));
		}
		
		/** update the constraint-based infection pair **/
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean logic_ior() throws Exception {
		/** declarations **/
		List<CirAttribute> constraints = new ArrayList<CirAttribute>();
		CirAttribute constraint, init_error; SymbolExpression condition;
		
		/** constraint: {x * y --> x || y} ==> (x != 0 || y != 0) **/
		condition = SymbolFactory.not_equals(this.loperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		condition = SymbolFactory.not_equals(this.roperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		constraint = this.disjunct(constraints);
		
		/** determine initial error from the mutation **/
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.logic_or, this.loperand, this.roperand));
		}
		
		/** update the constraint-based infection pair **/
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean greater_tn() throws Exception {
		/** constraint: {x * y --> x > y} : TRUE**/
		CirAttribute constraint = this.get_constraint(Boolean.TRUE);
		
		/** determine initial error from the mutation **/
		CirAttribute init_error;
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.greater_tn, this.loperand, this.roperand));
		}
		
		/** update the constraint-based infection pair **/
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean greater_eq() throws Exception {
		/** constraint: {x * y --> x >= y} : TRUE**/
		CirAttribute constraint = this.get_constraint(Boolean.TRUE);
		
		/** determine initial error from the mutation **/
		CirAttribute init_error;
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.greater_eq, this.loperand, this.roperand));
		}
		
		/** update the constraint-based infection pair **/
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean smaller_tn() throws Exception {
		/** constraint: {x * y --> x < y} : TRUE**/
		CirAttribute constraint = this.get_constraint(Boolean.TRUE);
		
		/** determine initial error from the mutation **/
		CirAttribute init_error;
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.smaller_tn, this.loperand, this.roperand));
		}
		
		/** update the constraint-based infection pair **/
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean smaller_eq() throws Exception {
		/** constraint: {x * y --> x <= y} : TRUE**/
		CirAttribute constraint = this.get_constraint(Boolean.TRUE);
		
		/** determine initial error from the mutation **/
		CirAttribute init_error;
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.smaller_eq, this.loperand, this.roperand));
		}
		
		/** update the constraint-based infection pair **/
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean equal_with() throws Exception {
		/** constraint: {x * y --> x == y} : TRUE**/
		CirAttribute constraint = this.get_constraint(Boolean.TRUE);
		
		/** determine initial error from the mutation **/
		CirAttribute init_error;
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.equal_with, this.loperand, this.roperand));
		}
		
		/** update the constraint-based infection pair **/
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean not_equals() throws Exception {
		/** constraint: {x * y --> x != y} **/
		CirAttribute constraint = this.get_constraint(Boolean.TRUE);
		
		/** determine initial error from the mutation **/
		CirAttribute init_error;
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.not_equals, this.loperand, this.roperand));
		}
		
		/** update the constraint-based infection pair **/
		return this.add_infection(constraint, init_error);
	}

}
