package com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirOperatorParser;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class CirSetBitwsAndParser extends CirOperatorParser {

	@Override
	protected boolean to_assign() throws Exception {
		CirAttribute constraint; CirAttribute init_error;
		constraint = this.get_constraint(this.sym_expression(
				COperator.not_equals, this.loperand, Integer.valueOf(~0)));
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
		 * 	[x != 0 || y != 0]
		 */
		CirAttribute constraint; CirAttribute init_error;
		SymbolExpression condition;
		List<CirAttribute> constraints = new ArrayList<>();

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
					COperator.arith_add, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_sub() throws Exception {
		/**
		 * 	[y != 0]
		 */
		CirAttribute constraint; CirAttribute init_error;
		constraint = this.get_constraint(this.sym_expression(
				COperator.not_equals, this.roperand, Integer.valueOf(0)));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.arith_sub, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_mul() throws Exception {
		/**
		 * [x != 0 && y != 0]
		 */
		CirAttribute constraint; CirAttribute init_error;
		SymbolExpression condition;
		List<CirAttribute> constraints = new ArrayList<>();

		condition = this.sym_expression(COperator.not_equals, loperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		condition = this.sym_expression(COperator.not_equals, roperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		constraint = this.conjunct(constraints);

		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.arith_add, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_div() throws Exception {
		/**
		 * [y == 0] --> trap_stmt()
		 * [x != 0] --> set_expr(x / y)
		 */
		CirAttribute constraint; CirAttribute init_error;
		if(this.compare_or_mutate) {
			constraint = this.get_constraint(this.sym_expression(
					COperator.not_equals, this.loperand, Integer.valueOf(0)));
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
		 * [y == 0] --> trap_stmt()
		 * [x != 0] --> set_expr(x % y)
		 */
		CirAttribute constraint; CirAttribute init_error;
		if(this.compare_or_mutate) {
			constraint = this.get_constraint(this.sym_expression(
					COperator.not_equals, this.loperand, Integer.valueOf(0)));
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
					COperator.arith_mod, this.loperand, this.roperand));
			return this.add_infection(constraint, init_error);
		}
	}

	@Override
	protected boolean bitws_and() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		/**
		 * [x != y]
		 */
		CirAttribute constraint; CirAttribute init_error;
		constraint = this.get_constraint(this.sym_expression(
				COperator.not_equals, this.loperand, this.roperand));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.bit_or, this.loperand, this.roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		/**
		 * 	[x != 0 || y != 0]
		 */
		CirAttribute constraint; CirAttribute init_error;
		SymbolExpression condition;
		List<CirAttribute> constraints = new ArrayList<>();

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
					COperator.bit_xor, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		/**
		 * [x != 0]
		 */
		CirAttribute constraint; CirAttribute init_error;
		constraint = this.get_constraint(this.sym_expression(
				COperator.not_equals, this.loperand, Integer.valueOf(0)));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.left_shift, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		CirAttribute constraint; CirAttribute init_error;
		constraint = this.get_constraint(this.sym_expression(
				COperator.not_equals, this.loperand, Integer.valueOf(0)));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.sym_expression(
					COperator.righ_shift, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean logic_and() throws Exception {
		/**
		 * [x != AnyBool || y != AnyBool]
		 */
		CirAttribute constraint; CirAttribute init_error;
		SymbolExpression condition, operand;
		List<CirAttribute> constraints = new ArrayList<>();

		operand = SymbolFactory.
				identifier(CBasicTypeImpl.bool_type, CirOperatorParser.AnyBoolean);
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
		 * 	[x != 0 || y != 0]
		 */
		CirAttribute constraint; CirAttribute init_error;
		SymbolExpression condition;
		List<CirAttribute> constraints = new ArrayList<>();

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
					COperator.logic_or, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean greater_tn() throws Exception {
		CirAttribute constraint = this.get_constraint(Boolean.TRUE);
		CirAttribute init_error;
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
		CirAttribute constraint = this.get_constraint(Boolean.TRUE);
		CirAttribute init_error;
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
		CirAttribute constraint = this.get_constraint(Boolean.TRUE);
		CirAttribute init_error;
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
		CirAttribute constraint = this.get_constraint(Boolean.TRUE);
		CirAttribute init_error;
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
		CirAttribute constraint = this.get_constraint(Boolean.TRUE);
		CirAttribute init_error;
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
		CirAttribute constraint = this.get_constraint(Boolean.TRUE);
		CirAttribute init_error;
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
