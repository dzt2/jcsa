package com.jcsa.jcmutest.backups;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class SetBitwsXorProcess extends SetOperatorProcess {

	@Override
	protected boolean to_assign() throws Exception {
		SecConstraint constraint; SecStateError init_error;
		constraint = this.get_constraint(this.sym_expression(
				COperator.not_equals, this.loperand, Integer.valueOf(0)));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.roperand);
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_add() throws Exception {
		/**
		 * [x & y != 0]
		 */
		SecConstraint constraint; SecStateError init_error; SymExpression condition;
		condition = this.sym_expression(COperator.bit_and, loperand, roperand);
		condition = this.sym_expression(COperator.not_equals, condition, Integer.valueOf(0));
		constraint = this.get_constraint(condition);
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.arith_add, this.loperand, this.roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_sub() throws Exception {
		/**
		 * [x & y != y] 
		 */
		SecConstraint constraint; SecStateError init_error; SymExpression condition;
		condition = this.sym_expression(COperator.bit_and, loperand, roperand);
		condition = this.sym_expression(COperator.not_equals, condition, roperand);
		constraint = this.get_constraint(condition);
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.arith_sub, this.loperand, this.roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_mul() throws Exception {
		/**
		 * [x != 0 || y != 0]
		 */
		SecConstraint constraint; SecStateError init_error; 
		SymExpression condition;
		List<SecConstraint> constraints = new ArrayList<SecConstraint>();
		
		condition = this.sym_expression(COperator.not_equals, loperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		condition = this.sym_expression(COperator.not_equals, roperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		constraint = this.disjunct(constraints);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.arith_mul, this.loperand, this.roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_div() throws Exception {
		/**
		 * 	[y == 0]
		 * 	[x != 0]
		 */
		SecConstraint constraint; SecStateError init_error;
		if(this.compare_or_mutate) {
			constraint = this.get_constraint(Boolean.TRUE);
			init_error = this.trap_statement();
			return this.add_infection(constraint, init_error);
		}
		else {
			constraint = this.get_constraint(this.sym_expression(
					COperator.equal_with, this.roperand, Integer.valueOf(0)));
			init_error = this.trap_statement();
			this.add_infection(constraint, init_error);
			
			constraint = this.get_constraint(this.sym_expression(
					COperator.not_equals, this.loperand, Integer.valueOf(0)));
			init_error = this.set_expression(this.sym_expression(
					COperator.arith_div, this.loperand, this.roperand));
			return this.add_infection(constraint, init_error);
		}
	}

	@Override
	protected boolean arith_mod() throws Exception {
		/**
		 * [x != y] -->
		 */
		SecConstraint constraint; SecStateError init_error;
		constraint = this.get_constraint(this.sym_expression(COperator.not_equals, loperand, roperand));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(COperator.arith_mod, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_and() throws Exception {
		/**
		 * [x != 0 || y != 0]
		 */
		SecConstraint constraint; SecStateError init_error; 
		SymExpression condition;
		List<SecConstraint> constraints = new ArrayList<SecConstraint>();
		
		condition = this.sym_expression(COperator.not_equals, loperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		condition = this.sym_expression(COperator.not_equals, roperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		constraint = this.disjunct(constraints);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.bit_and, this.loperand, this.roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		/**
		 * [x != y]
		 */
		SecConstraint constraint; SecStateError init_error;
		constraint = this.get_constraint(this.sym_expression(COperator.not_equals, loperand, roperand));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(COperator.bit_or, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		/**
		 * [y != 0]
		 */
		SecConstraint constraint; SecStateError init_error;
		constraint = this.get_constraint(this.sym_expression(COperator.not_equals, roperand, Integer.valueOf(0)));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(COperator.left_shift, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		/**
		 * [y != 0]
		 */
		SecConstraint constraint; SecStateError init_error;
		constraint = this.get_constraint(this.sym_expression(COperator.not_equals, roperand, Integer.valueOf(0)));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(COperator.righ_shift, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean logic_and() throws Exception {
		/**
		 * [x != 0 || y != 0]
		 */
		SecConstraint constraint; SecStateError init_error; 
		SymExpression condition;
		List<SecConstraint> constraints = new ArrayList<SecConstraint>();
		
		condition = this.sym_expression(COperator.not_equals, loperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		condition = this.sym_expression(COperator.not_equals, roperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		constraint = this.disjunct(constraints);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(Integer.valueOf(1));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean logic_ior() throws Exception {
		/**
		 * [x != 0 || y != 0]
		 */
		SecConstraint constraint; SecStateError init_error; 
		SymExpression condition;
		List<SecConstraint> constraints = new ArrayList<SecConstraint>();
		
		condition = this.sym_expression(COperator.not_equals, loperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		condition = this.sym_expression(COperator.not_equals, roperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		constraint = this.disjunct(constraints);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(Integer.valueOf(1));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean greater_tn() throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE);
		SecStateError init_error;
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.greater_tn, this.loperand, this.roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean greater_eq() throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE);
		SecStateError init_error;
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.greater_eq, this.loperand, this.roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean smaller_tn() throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE);
		SecStateError init_error;
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.smaller_tn, this.loperand, this.roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean smaller_eq() throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE);
		SecStateError init_error;
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.smaller_eq, this.loperand, this.roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean equal_with() throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE);
		SecStateError init_error;
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.equal_with, this.loperand, this.roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean not_equals() throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE);
		SecStateError init_error;
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.not_equals, this.loperand, this.roperand));
		}
		return this.add_infection(constraint, init_error);
	}
	
}
