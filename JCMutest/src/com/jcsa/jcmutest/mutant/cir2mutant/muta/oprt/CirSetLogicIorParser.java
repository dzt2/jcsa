package com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.SymStateError;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirSetOperatorParser;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.sym.SymExpression;

public class CirSetLogicIorParser extends CirSetOperatorParser {

	@Override
	protected boolean to_assign() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean arith_add() throws Exception {
		return this.report_equivalence_mutation();
	}

	@Override
	protected boolean arith_sub() throws Exception {
		/**
		 * [B(x) && B(y)] --> set_false
		 */
		SymConstraint constraint; SymStateError init_error; 
		SymExpression condition;
		List<SymConstraint> constraints = new ArrayList<SymConstraint>();
		
		condition = this.sym_condition(this.loperand, true);
		constraints.add(this.get_constraint(condition));
		condition = this.sym_condition(this.roperand, true);
		constraints.add(this.get_constraint(condition));
		constraint = this.conjunct(constraints);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(Boolean.FALSE);
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_mul() throws Exception {
		/**
		 * B(x) != B(y) --> set_false
		 */
		SymConstraint constraint; SymStateError init_error;
		SymExpression lcondition, rcondition, condition;
		
		lcondition = this.sym_condition(this.loperand, true);
		rcondition = this.sym_condition(this.roperand, true);
		condition = this.sym_expression(COperator.
					not_equals, lcondition, rcondition);
		constraint = this.get_constraint(condition);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(Boolean.FALSE);
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_div() throws Exception {
		/**
		 * 	[!B(y)] --> trap
		 * 	[!B(x) && B(y)] --> set_false
		 */
		SymConstraint constraint; SymStateError init_error; 
		SymExpression condition;
		List<SymConstraint> constraints = new ArrayList<SymConstraint>();
		
		if(this.compare_or_mutate) {
			condition = this.sym_condition(this.loperand, false);
			constraints.add(this.get_constraint(condition));
			condition = this.sym_condition(this.roperand, false);
			constraints.add(this.get_constraint(condition));
			constraint = this.disjunct(constraints);
			init_error = this.trap_statement();
			return this.add_infection(constraint, init_error);
		}
		else {
			condition = this.sym_condition(this.roperand, false);
			constraint = this.get_constraint(condition);
			init_error = this.trap_statement();
			this.add_infection(constraint, init_error);
			
			condition = this.sym_condition(this.loperand, false);
			constraints.add(this.get_constraint(condition));
			condition = this.sym_condition(this.roperand, true);
			constraints.add(this.get_constraint(condition));
			constraint = this.conjunct(constraints);
			init_error = this.set_expression(Boolean.FALSE);
			return this.add_infection(constraint, init_error);
		}
	}

	@Override
	protected boolean arith_mod() throws Exception {
		/**
		 * [B(y)] --> set_false
		 * [!B(y)] --> trap()
		 */
		SymConstraint constraint; SymStateError init_error; 
		SymExpression condition;
		
		if(this.compare_or_mutate) {
			constraint = this.get_constraint(Boolean.TRUE);
			init_error = this.trap_statement();
			return this.add_infection(constraint, init_error);
		}
		else {
			condition = this.sym_condition(this.roperand, false);
			constraint = this.get_constraint(condition);
			init_error = this.trap_statement();
			this.add_infection(constraint, init_error);
			
			condition = this.sym_condition(this.roperand, true);
			constraint = this.get_constraint(condition);
			init_error = this.set_expression(Boolean.FALSE);
			return this.add_infection(constraint, init_error);
		}
	}

	@Override
	protected boolean bitws_and() throws Exception {
		/**
		 * B(x) != B(y) --> set_false
		 */
		SymConstraint constraint; SymStateError init_error;
		SymExpression lcondition, rcondition, condition;
		
		lcondition = this.sym_condition(this.loperand, true);
		rcondition = this.sym_condition(this.roperand, true);
		condition = this.sym_expression(COperator.
					not_equals, lcondition, rcondition);
		constraint = this.get_constraint(condition);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(Boolean.FALSE);
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		return this.report_equivalence_mutation();
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		/**
		 * [B(x) && B(y)] --> set_false
		 */
		SymConstraint constraint; SymStateError init_error; 
		SymExpression condition;
		List<SymConstraint> constraints = new ArrayList<SymConstraint>();
		
		condition = this.sym_condition(this.loperand, true);
		constraints.add(this.get_constraint(condition));
		condition = this.sym_condition(this.roperand, true);
		constraints.add(this.get_constraint(condition));
		constraint = this.conjunct(constraints);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(Boolean.FALSE);
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		/**
		 * [!B(x) && B(y)] --> set_false
		 */
		SymConstraint constraint; SymStateError init_error; 
		SymExpression condition;
		List<SymConstraint> constraints = new ArrayList<SymConstraint>();
		
		condition = this.sym_condition(this.loperand, false);
		constraints.add(this.get_constraint(condition));
		condition = this.sym_condition(this.roperand, true);
		constraints.add(this.get_constraint(condition));
		constraint = this.conjunct(constraints);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(Boolean.FALSE);
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		/**
		 * [B(y)] --> set_false
		 */
		SymConstraint constraint; SymStateError init_error;
		constraint = this.get_constraint(
					this.sym_condition(this.roperand, true));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(Boolean.FALSE);
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean logic_and() throws Exception {
		/**
		 * B(x) != B(y) --> set_false
		 */
		SymConstraint constraint; SymStateError init_error;
		SymExpression lcondition, rcondition, condition;
		
		lcondition = this.sym_condition(this.loperand, true);
		rcondition = this.sym_condition(this.roperand, true);
		condition = this.sym_expression(COperator.
					not_equals, lcondition, rcondition);
		constraint = this.get_constraint(condition);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(Boolean.FALSE);
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean logic_ior() throws Exception {
		return this.report_equivalence_mutation();
	}

	@Override
	protected boolean greater_tn() throws Exception {
		/**
		 * [B(y)] --> set_false
		 */
		SymConstraint constraint; SymStateError init_error;
		constraint = this.get_constraint(
					this.sym_condition(this.roperand, true));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(Boolean.FALSE);
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean greater_eq() throws Exception {
		/**
		 * [!B(x)] --> not_expr
		 */
		SymConstraint constraint; SymStateError init_error;
		constraint = this.get_constraint(
					this.sym_condition(this.loperand, false));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.not_expression();
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean smaller_tn() throws Exception {
		/**
		 * [B(x)] --> set_false
		 */
		SymConstraint constraint; SymStateError init_error;
		constraint = this.get_constraint(
					this.sym_condition(this.loperand, true));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(Boolean.FALSE);
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean smaller_eq() throws Exception {
		/**
		 * [!B(y)] --> not_expr
		 */
		SymConstraint constraint; SymStateError init_error;
		constraint = this.get_constraint(
					this.sym_condition(this.roperand, false));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.not_expression();
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean equal_with() throws Exception {
		/**
		 * [!B(x) || !B(y)] --> not_expr
		 */
		SymConstraint constraint; SymStateError init_error; 
		SymExpression condition;
		List<SymConstraint> constraints = new ArrayList<SymConstraint>();
		
		condition = this.sym_condition(this.loperand, false);
		constraints.add(this.get_constraint(condition));
		condition = this.sym_condition(this.roperand, false);
		constraints.add(this.get_constraint(condition));
		constraint = this.disjunct(constraints);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.not_expression();
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean not_equals() throws Exception {
		/**
		 * [B(x) && B(y)] --> set_false
		 */
		SymConstraint constraint; SymStateError init_error; 
		SymExpression condition;
		List<SymConstraint> constraints = new ArrayList<SymConstraint>();
		
		condition = this.sym_condition(this.loperand, true);
		constraints.add(this.get_constraint(condition));
		condition = this.sym_condition(this.roperand, true);
		constraints.add(this.get_constraint(condition));
		constraint = this.conjunct(constraints);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(Boolean.TRUE);
		}
		return this.add_infection(constraint, init_error);
	}

}
