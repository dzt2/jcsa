package com.jcsa.jcmutest.mutant.sec2mutant.muta.proc;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SetOperatorProcess;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;
import com.jcsa.jcparse.lang.sym.SymIdentifier;

public class SetArithAddProcess extends SetOperatorProcess {

	@Override
	protected boolean to_assign() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean arith_add() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean arith_sub() throws Exception {
		/**
		 * 	[y != 0] --> set_expr(x - y)
		 **/
		SecConstraint constraint; SecDescription init_error;
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
		 * 	[x != 0 || y != 0] --> set_expr(x * y)
		 **/
		SecDescription constraint, init_error; SymExpression condition;
		List<SecDescription> constraints = new ArrayList<SecDescription>();
		
		condition = this.sym_expression(COperator.not_equals, this.loperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		condition = this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		
		constraint = this.disjunct(constraints);
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(sym_expression(COperator.arith_mul, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_div() throws Exception {
		/**
		 * 	[y == 0] --> trap_stmt()
		 * 	[y != 0] --> set_expr(x / y)
		 **/
		SecConstraint constraint; SecDescription init_error;
		if(this.compare_or_mutate) {
			constraint = this.get_constraint(Boolean.TRUE);
			init_error = this.trap_statement();
			return this.add_infection(constraint, init_error);
		}
		else {
			constraint = this.get_constraint(sym_expression(COperator.
						equal_with, this.roperand, Integer.valueOf(0)));
			init_error = this.trap_statement();
			this.add_infection(constraint, init_error);
			
			constraint = this.get_constraint(sym_expression(COperator.
						not_equals, this.roperand, Integer.valueOf(0)));
			init_error = this.set_expression(this.sym_expression(
					COperator.arith_div, this.loperand, this.roperand));
			return this.add_infection(constraint, init_error);
		}
	}

	@Override
	protected boolean arith_mod() throws Exception {
		/**
		 * 	[y == 0] --> trap_statement()
		 * 	[x < -y || x > -2y] --> set_expr
		 */
		SecDescription constraint, init_error; SymExpression condition, operand;
		List<SecDescription> constraints = new ArrayList<SecDescription>();
		if(this.compare_or_mutate) {
			operand = this.sym_expression(COperator.negative, this.roperand);
			condition = this.sym_expression(COperator.smaller_tn, this.loperand, operand);
			constraints.add(this.get_constraint(condition));
			
			operand = this.sym_expression(COperator.arith_mul, Integer.valueOf(-2), roperand);
			condition = this.sym_expression(COperator.greater_tn, this.loperand, operand);
			constraints.add(this.get_constraint(condition));
			
			constraint = this.disjunct(constraints);
			init_error = this.trap_statement();
			return this.add_infection(constraint, init_error);
		}
		else {
			condition = this.sym_expression(COperator.equal_with, roperand, Integer.valueOf(0));
			this.add_infection(this.get_constraint(condition), this.trap_statement());
			
			operand = this.sym_expression(COperator.negative, this.roperand);
			condition = this.sym_expression(COperator.smaller_tn, this.loperand, operand);
			constraints.add(this.get_constraint(condition));
			
			operand = this.sym_expression(COperator.arith_mul, Integer.valueOf(-2), roperand);
			condition = this.sym_expression(COperator.greater_tn, this.loperand, operand);
			constraints.add(this.get_constraint(condition));
			
			constraint = this.disjunct(constraints);
			init_error = this.set_expression(this.sym_expression(
					COperator.arith_mod, this.loperand, this.roperand));
			return this.add_infection(constraint, init_error);
		}
	}

	@Override
	protected boolean bitws_and() throws Exception {
		/**
		 * 	[x != 0 || y != 0] --> set_expr(x & y)
		 **/
		SecDescription constraint, init_error; SymExpression condition;
		List<SecDescription> constraints = new ArrayList<SecDescription>();
		
		condition = this.sym_expression(COperator.not_equals, this.loperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		condition = this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		
		constraint = this.disjunct(constraints);
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(sym_expression(COperator.bit_and, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		/**
		 * 	[x & y != 0] --> set_expr(x | y)
		 **/
		SymExpression operand, condition;
		SecConstraint constraint; SecDescription init_error;
		
		operand = this.sym_expression(COperator.bit_and, this.loperand, this.roperand);
		condition = this.sym_expression(COperator.not_equals, operand, Integer.valueOf(0));
		constraint = this.get_constraint(condition);
		
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
		 * 	[x & y != 0] --> set_expr(x ^ y)
		 **/
		SymExpression operand, condition;
		SecConstraint constraint; SecDescription init_error;
		
		operand = this.sym_expression(COperator.bit_and, this.loperand, this.roperand);
		condition = this.sym_expression(COperator.not_equals, operand, Integer.valueOf(0));
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
		 * 	[y != 0] --> set_expr(x << y)
		 */
		SecConstraint constraint; SecDescription init_error;
		constraint = this.get_constraint(sym_expression(COperator.
					not_equals, this.roperand, Integer.valueOf(0)));
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
		 * 	[y != 0] --> set_expr(x >> y)
		 */
		SecConstraint constraint; SecDescription init_error;
		constraint = this.get_constraint(sym_expression(COperator.
					not_equals, this.roperand, Integer.valueOf(0)));
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
		 * 	[x != 0 || y != 0] --> set_expr(x && y)
		 **/
		SecDescription constraint, init_error; SymExpression condition;
		List<SecDescription> constraints = new ArrayList<SecDescription>();
		
		condition = this.sym_expression(COperator.not_equals, this.loperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		condition = this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		
		constraint = this.disjunct(constraints);
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(sym_expression(COperator.logic_and, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean logic_ior() throws Exception {
		/**
		 * 	[x != AnyBool || y != AnyBool]
		 */
		SecDescription constraint, init_error; SymExpression operand;
		List<SecDescription> constraints = new ArrayList<SecDescription>();
		
		operand = SymFactory.new_identifier(CBasicTypeImpl.bool_type, SymIdentifier.AnyBoolean);
		constraints.add(get_constraint(sym_expression(COperator.not_equals, loperand, operand)));
		constraints.add(get_constraint(sym_expression(COperator.not_equals, roperand, operand)));
		constraint = this.disjunct(constraints);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = set_expression(sym_expression(COperator.logic_or, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean greater_tn() throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE);
		SecDescription init_error;
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
		SecDescription init_error;
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
		SecDescription init_error;
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
		SecDescription init_error;
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
		SecDescription init_error;
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
		SecDescription init_error;
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
