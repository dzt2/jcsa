package com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.model.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirSetOperatorParser;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class CirSetArithModParser extends CirSetOperatorParser {

	@Override
	protected boolean to_assign() throws Exception {
		CirConstraint constraint; CirStateError init_error;
		constraint = this.get_constraint(Boolean.TRUE);
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			constraint = this.get_constraint(this.sym_expression(
					COperator.equal_with, this.loperand, Integer.valueOf(0)));
			init_error = this.trap_statement();
			this.add_infection(constraint, init_error);
			
			constraint = this.get_constraint(this.sym_expression(
					COperator.not_equals, this.loperand, Integer.valueOf(0))); 
			init_error = this.set_expression(this.roperand);
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_add() throws Exception {
		/**
		 * 	[y == 0] --> trap_statement()
		 * 	[x < -y || x > -2y] --> set_expr
		 */
		CirConstraint constraint; CirStateError init_error; 
		SymExpression condition, operand;
		List<CirConstraint> constraints = new ArrayList<CirConstraint>();
		
		operand = this.sym_expression(COperator.negative, this.roperand);
		condition = this.sym_expression(COperator.smaller_tn, this.loperand, operand);
		constraints.add(this.get_constraint(condition));
		operand = this.sym_expression(COperator.arith_mul, Integer.valueOf(-2), roperand);
		condition = this.sym_expression(COperator.greater_tn, this.loperand, operand);
		constraints.add(this.get_constraint(condition));
		constraint = this.disjunct(constraints);
		
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
		 * 	[y == 0] --> trap_statement()
		 * 	[x < y || x > 2y] --> set_expr
		 */
		CirConstraint constraint; CirStateError init_error; 
		SymExpression condition, operand;
		List<CirConstraint> constraints = new ArrayList<CirConstraint>();
		
		operand = this.sym_expression(COperator.positive, this.roperand);
		condition = this.sym_expression(COperator.smaller_tn, this.loperand, operand);
		constraints.add(this.get_constraint(condition));
		operand = this.sym_expression(COperator.arith_mul, Integer.valueOf(2), roperand);
		condition = this.sym_expression(COperator.greater_tn, this.loperand, operand);
		constraints.add(this.get_constraint(condition));
		constraint = this.disjunct(constraints);
		
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
		 * [x != 0]
		 */
		CirConstraint constraint; CirStateError init_error;
		constraint = this.get_constraint(this.sym_expression(COperator.not_equals, loperand, Integer.valueOf(0)));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(COperator.arith_mul, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_div() throws Exception {
		/**
		 * [x != 0]
		 */
		CirConstraint constraint; CirStateError init_error;
		constraint = this.get_constraint(this.sym_expression(COperator.not_equals, loperand, Integer.valueOf(0)));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(COperator.arith_div, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_mod() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean bitws_and() throws Exception {
		/**
		 * [x != 0]
		 */
		CirConstraint constraint; CirStateError init_error;
		constraint = this.get_constraint(this.sym_expression(COperator.not_equals, loperand, Integer.valueOf(0)));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(COperator.bit_and, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		/**
		 * [true]
		 */
		CirConstraint constraint; CirStateError init_error;
		constraint = this.get_constraint(Boolean.TRUE);
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
		/**
		 * [x != y] -->
		 */
		CirConstraint constraint; CirStateError init_error;
		constraint = this.get_constraint(this.sym_expression(COperator.not_equals, loperand, roperand));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(COperator.bit_xor, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		/**
		 * [x != 0]
		 */
		CirConstraint constraint; CirStateError init_error;
		constraint = this.get_constraint(this.sym_expression(COperator.not_equals, loperand, Integer.valueOf(0)));
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
		 * [x != 0]
		 */
		CirConstraint constraint; CirStateError init_error;
		constraint = this.get_constraint(this.sym_expression(COperator.not_equals, loperand, Integer.valueOf(0)));
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
		 * [x != 0]
		 */
		CirConstraint constraint; CirStateError init_error;
		constraint = this.get_constraint(this.sym_expression(COperator.not_equals, loperand, Integer.valueOf(0)));
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
		 * [true]
		 */
		CirConstraint constraint; CirStateError init_error;
		constraint = this.get_constraint(Boolean.TRUE);
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
		CirConstraint constraint = this.get_constraint(Boolean.TRUE);
		CirStateError init_error;
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
		CirConstraint constraint = this.get_constraint(Boolean.TRUE);
		CirStateError init_error;
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
		CirConstraint constraint = this.get_constraint(Boolean.TRUE);
		CirStateError init_error;
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
		CirConstraint constraint = this.get_constraint(Boolean.TRUE);
		CirStateError init_error;
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
		CirConstraint constraint = this.get_constraint(Boolean.TRUE);
		CirStateError init_error;
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
		CirConstraint constraint = this.get_constraint(Boolean.TRUE);
		CirStateError init_error;
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
