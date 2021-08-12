package com.jcsa.jcmutest.mutant.cir2mutant.muta.opra;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirOperatorParser;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class CirSetLogicIorParser extends CirOperatorParser {

	@Override
	protected boolean to_assign() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean arith_add() throws Exception {
		CirAttribute constraint = this.get_constraint(Boolean.FALSE);
		CirAttribute init_error;
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.expression);
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_sub() throws Exception {
		/** declarations **/
		List<CirAttribute> constraints = new ArrayList<CirAttribute>();
		CirAttribute constraint, init_error; SymbolExpression condition;
		
		/** (TRUE, TRUE) --> FALSE **/
		condition = this.sym_condition(this.loperand, Boolean.TRUE);
		constraints.add(this.get_constraint(condition));
		condition = this.sym_condition(this.roperand, Boolean.TRUE);
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
		return this.logic_and();
	}

	@Override
	protected boolean arith_div() throws Exception {
		CirAttribute constraint, init_error; SymbolExpression condition;
		
		/** (XXXX, FALSE) ==> trap_stmt **/
		condition = this.sym_condition(this.roperand, false);
		constraint = this.get_constraint(condition);
		init_error = this.trap_statement();
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_mod() throws Exception {
		List<CirAttribute> constraints = new ArrayList<CirAttribute>();
		CirAttribute constraint, init_error; SymbolExpression condition;
		
		/** (XXXX, FALSE) ==> trap_stmt **/
		condition = this.sym_condition(this.roperand, false);
		constraint = this.get_constraint(condition);
		init_error = this.trap_statement();
		this.add_infection(constraint, init_error);
		
		/** (TRUE, TRUE) ==> FALSE **/
		condition = this.sym_condition(this.loperand, Boolean.TRUE);
		constraints.add(this.get_constraint(condition));
		condition = this.sym_condition(this.roperand, Boolean.TRUE);
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
	protected boolean bitws_and() throws Exception {
		return this.logic_and();
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		CirAttribute constraint = this.get_constraint(Boolean.FALSE);
		CirAttribute init_error;
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(this.expression);
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		/** declarations **/
		List<CirAttribute> constraints = new ArrayList<CirAttribute>();
		CirAttribute constraint, init_error; SymbolExpression condition;
		
		/** (TRUE, TRUE) --> FALSE **/
		condition = this.sym_condition(this.loperand, Boolean.TRUE);
		constraints.add(this.get_constraint(condition));
		condition = this.sym_condition(this.roperand, Boolean.TRUE);
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
		/** declarations **/
		List<CirAttribute> constraints = new ArrayList<CirAttribute>();
		CirAttribute constraint, init_error; SymbolExpression condition;
		
		/** (FALSE, TRUE) --> FALSE **/
		condition = this.sym_condition(this.loperand, Boolean.FALSE);
		constraints.add(this.get_constraint(condition));
		condition = this.sym_condition(this.roperand, Boolean.TRUE);
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
		/** declarations **/
		List<CirAttribute> constraints = new ArrayList<CirAttribute>();
		CirAttribute constraint, init_error; SymbolExpression condition;
		
		/** (FALSE, TRUE) --> FALSE **/
		condition = this.sym_condition(this.loperand, Boolean.FALSE);
		constraints.add(this.get_constraint(condition));
		condition = this.sym_condition(this.roperand, Boolean.TRUE);
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
	protected boolean logic_and() throws Exception {
		/** declarations **/
		List<CirAttribute> constraints = new ArrayList<CirAttribute>();
		CirAttribute constraint, init_error; SymbolExpression condition;
		
		/** (TRUE, FALSE) --> FALSE **/
		constraints.clear();
		condition = this.sym_condition(this.loperand, Boolean.TRUE);
		constraints.add(this.get_constraint(condition));
		condition = this.sym_condition(this.roperand, Boolean.FALSE);
		constraints.add(this.get_constraint(condition));
		constraint = this.conjunct(constraints);
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(Boolean.FALSE);
		}
		this.add_infection(constraint, init_error);
		
		/** (FALSE, TRUE) --> FALSE **/
		constraints.clear();
		condition = this.sym_condition(this.loperand, Boolean.FALSE);
		constraints.add(this.get_constraint(condition));
		condition = this.sym_condition(this.roperand, Boolean.TRUE);
		constraints.add(this.get_constraint(condition));
		constraint = this.conjunct(constraints);
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(Boolean.FALSE);
		}
		this.add_infection(constraint, init_error);
		
		return true;
	}

	@Override
	protected boolean logic_ior() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean greater_tn() throws Exception {
		/**	constraint-error pair
		 * 	1. (f, f) :: [f --> f]
		 * 	2. (f, t) :: [t --> f]
		 *  3. (t, f) :: [t --> t]
		 *  4. (t, t) :: [t --> f]
		 * **/
		CirAttribute constraint, init_error;
		constraint = this.get_constraint(this.sym_condition(this.roperand, true));
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
		/**	constraint-error pair
		 * 	1. (f, f) :: [f --> t]
		 * 	2. (f, t) :: [t --> f]
		 *  3. (t, f) :: [t --> t]
		 *  4. (t, t) :: [t --> t]
		 * **/
		CirAttribute constraint, init_error;
		constraint = this.get_constraint(this.sym_condition(this.loperand, false));
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
		/**	constraint-error pair
		 * 	1. (f, f) :: [f --> f]
		 * 	2. (f, t) :: [t --> t]
		 *  3. (t, f) :: [t --> f]
		 *  4. (t, t) :: [t --> f]
		 * **/
		CirAttribute constraint, init_error;
		constraint = this.get_constraint(this.sym_condition(this.loperand, true));
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
		/**	constraint-error pair
		 * 	1. (f, f) :: [f --> t]
		 * 	2. (f, t) :: [t --> t]
		 *  3. (t, f) :: [t --> f]
		 *  4. (t, t) :: [t --> t]
		 * **/
		CirAttribute constraint, init_error;
		constraint = this.get_constraint(this.sym_condition(this.roperand, false));
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
		/**	constraint-error pair
		 * 	1. (f, f) :: [f --> t]
		 * 	2. (f, t) :: [t --> f]
		 *  3. (t, f) :: [t --> f]
		 *  4. (t, t) :: [t --> t]
		 * **/
		List<CirAttribute> constraints = new ArrayList<CirAttribute>();
		CirAttribute constraint, init_error; SymbolExpression condition;
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
		/**	constraint-error pair
		 * 	1. (f, f) :: [f --> f]
		 * 	2. (f, t) :: [t --> t]
		 *  3. (t, f) :: [t --> t]
		 *  4. (t, t) :: [t --> f]
		 * **/
		List<CirAttribute> constraints = new ArrayList<CirAttribute>();
		CirAttribute constraint, init_error; SymbolExpression condition;
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

}
