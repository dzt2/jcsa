package com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirSetOperatorParser;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;
import com.jcsa.jcparse.lang.sym.SymIdentifier;

public class CirSetBitwsIorParser extends CirSetOperatorParser {

	@Override
	protected boolean to_assign() throws Exception {
		CirConstraint constraint; CirStateError init_error;
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
		CirConstraint constraint; CirStateError init_error; SymExpression condition;
		
		condition = sym_expression(COperator.bit_and, loperand, roperand);
		condition = sym_expression(COperator.not_equals, condition, Integer.valueOf(0));
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
		 * [y != 0]
		 */
		CirConstraint constraint; CirStateError init_error;
		constraint = this.get_constraint(this.sym_expression(
				COperator.not_equals, this.roperand, Integer.valueOf(0)));
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
		CirConstraint constraint; CirStateError init_error; 
		SymExpression condition;
		List<CirConstraint> constraints = new ArrayList<CirConstraint>();
		
		condition = this.sym_expression(COperator.not_equals, loperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		condition = this.sym_expression(COperator.not_equals, roperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		constraint  =this.disjunct(constraints);
		
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
		 * 	[y == 0] --> trap_stmt()
		 * 	[y != 0] --> set_expr(
		 */
		CirConstraint constraint; CirStateError init_error;
		if(this.compare_or_mutate) {
			constraint = this.get_constraint(Boolean.TRUE);
			init_error = this.trap_statement();
			return this.add_infection(constraint, init_error);
		}
		else {
			constraint = this.get_constraint(this.sym_expression(
					COperator.equal_with, roperand, Integer.valueOf(0)));
			init_error = this.trap_statement();
			this.add_infection(constraint, init_error);
			
			constraint = this.get_constraint(this.sym_expression(
					COperator.not_equals, roperand, Integer.valueOf(0)));
			init_error = this.set_expression(this.sym_expression(
					COperator.arith_div, this.loperand, this.roperand));
			return this.add_infection(constraint, init_error);
		}
	}

	@Override
	protected boolean arith_mod() throws Exception {
		/**
		 * 	[y == 0] --> trap_stmt()
		 * 	[y != 0] --> set_expr(
		 */
		CirConstraint constraint; CirStateError init_error;
		if(this.compare_or_mutate) {
			constraint = this.get_constraint(Boolean.TRUE);
			init_error = this.trap_statement();
			return this.add_infection(constraint, init_error);
		}
		else {
			constraint = this.get_constraint(this.sym_expression(
					COperator.equal_with, roperand, Integer.valueOf(0)));
			init_error = this.trap_statement();
			this.add_infection(constraint, init_error);
			
			constraint = this.get_constraint(this.sym_expression(
					COperator.not_equals, roperand, Integer.valueOf(0)));
			init_error = this.set_expression(this.sym_expression(
					COperator.arith_mod, this.loperand, this.roperand));
			return this.add_infection(constraint, init_error);
		}
	}

	@Override
	protected boolean bitws_and() throws Exception {
		/**
		 * 	[x != y]
		 */
		CirConstraint constraint; CirStateError init_error;
		constraint = this.get_constraint(this.sym_expression(
				COperator.not_equals, this.loperand, this.roperand));
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
		return this.unsupport_exception();
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		/**
		 * [x & y != 0]
		 */
		CirConstraint constraint; CirStateError init_error; SymExpression condition;
		condition = this.sym_expression(COperator.bit_and, loperand, roperand);
		condition = this.sym_expression(COperator.not_equals, condition, Integer.valueOf(0));
		constraint = this.get_constraint(condition);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.bit_xor, this.loperand, this.roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		/**
		 * [y != 0]
		 */
		CirConstraint constraint; CirStateError init_error;
		constraint = this.get_constraint(this.sym_expression(
				COperator.not_equals, this.roperand, Integer.valueOf(0)));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.left_shift, this.loperand, this.roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		/**
		 * [y != 0]
		 */
		CirConstraint constraint; CirStateError init_error;
		constraint = this.get_constraint(this.sym_expression(
				COperator.not_equals, this.roperand, Integer.valueOf(0)));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.righ_shift, this.loperand, this.roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean logic_and() throws Exception {
		/**
		 * [x != 0 || y != 0]
		 */
		CirConstraint constraint; CirStateError init_error; 
		SymExpression condition, operand;
		List<CirConstraint> constraints = new ArrayList<CirConstraint>();
		
		operand = SymFactory.new_constant(Integer.valueOf(0));
		condition = this.sym_expression(COperator.not_equals, this.loperand, operand);
		constraints.add(this.get_constraint(condition));
		condition = this.sym_expression(COperator.not_equals, this.roperand, operand);
		constraints.add(this.get_constraint(condition));
		constraint = this.disjunct(constraints);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.
					sym_expression(COperator.logic_and, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean logic_ior() throws Exception {
		/**
		 * [x != AnyBool || y != AnyBool]
		 */
		CirConstraint constraint; CirStateError init_error; 
		SymExpression condition, operand;
		List<CirConstraint> constraints = new ArrayList<CirConstraint>();
		
		operand = SymFactory.
				new_identifier(CBasicTypeImpl.bool_type, SymIdentifier.AnyBoolean);
		condition = this.sym_expression(COperator.not_equals, this.loperand, operand);
		constraints.add(this.get_constraint(condition));
		condition = this.sym_expression(COperator.not_equals, this.roperand, operand);
		constraints.add(this.get_constraint(condition));
		constraint = this.disjunct(constraints);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.
					sym_expression(COperator.logic_or, loperand, roperand));
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
