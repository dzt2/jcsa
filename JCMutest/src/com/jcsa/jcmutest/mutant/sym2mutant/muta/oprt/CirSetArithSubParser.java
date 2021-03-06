package com.jcsa.jcmutest.mutant.sym2mutant.muta.oprt;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.sym2mutant.base.SymConstraint;
import com.jcsa.jcmutest.mutant.sym2mutant.base.SymStateError;
import com.jcsa.jcmutest.mutant.sym2mutant.muta.CirSetOperatorParser;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;


public class CirSetArithSubParser extends CirSetOperatorParser {

	protected boolean to_assign() throws Exception {
		SymConstraint constraint; SymStateError init_error;
		constraint = this.get_constraint(Boolean.TRUE);
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
		 * 	[y != 0] --> add_expr(expr, +, 2 * y)
		 **/
		SymConstraint constraint; SymStateError init_error;
		constraint = this.get_constraint(this.sym_expression(
				COperator.not_equals, this.roperand, Integer.valueOf(0)));
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.add_expression(this.sym_expression(
					COperator.arith_mul, Integer.valueOf(2), roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_sub() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean arith_mul() throws Exception {
		/**
		 * 	[x != 0 || y != 0] --> set_expr(x * y)
		 **/
		SymConstraint constraint; SymStateError init_error; 
		SymbolExpression condition;
		List<SymConstraint> constraints = new ArrayList<SymConstraint>();
		
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
		SymConstraint constraint; SymStateError init_error;
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
		 * 	[x < y || x > 2y] --> set_expr
		 */
		SymConstraint constraint; SymStateError init_error; 
		SymbolExpression condition, operand;
		List<SymConstraint> constraints = new ArrayList<SymConstraint>();
		if(this.compare_or_mutate) {
			operand = this.sym_expression(COperator.positive, this.roperand);
			condition = this.sym_expression(COperator.smaller_tn, this.loperand, operand);
			constraints.add(this.get_constraint(condition));
			
			operand = this.sym_expression(COperator.arith_mul, Integer.valueOf(2), roperand);
			condition = this.sym_expression(COperator.greater_tn, this.loperand, operand);
			constraints.add(this.get_constraint(condition));
			
			constraint = this.disjunct(constraints);
			init_error = this.trap_statement();
			return this.add_infection(constraint, init_error);
		}
		else {
			condition = this.sym_expression(COperator.equal_with, roperand, Integer.valueOf(0));
			this.add_infection(this.get_constraint(condition), this.trap_statement());
			
			operand = this.sym_expression(COperator.positive, this.roperand);
			condition = this.sym_expression(COperator.smaller_tn, this.loperand, operand);
			constraints.add(this.get_constraint(condition));
			
			operand = this.sym_expression(COperator.arith_mul, Integer.valueOf(2), roperand);
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
		 * 	[x != 0 || y != 0] --> set_expr(x * y)
		 **/
		SymConstraint constraint; SymStateError init_error; 
		SymbolExpression condition;
		List<SymConstraint> constraints = new ArrayList<SymConstraint>();
		
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
		 * 	[y != 0] --> set_expr(x | y)
		 */
		SymConstraint constraint; SymStateError init_error;
		constraint = this.get_constraint(this.sym_expression(
				COperator.not_equals, roperand, Integer.valueOf(0)));
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
		 * 	[x & y != y] --> set_expr(x ^ y)
		 */
		SymConstraint constraint; SymStateError init_error;
		SymbolExpression operand, condition;
		
		operand = this.sym_expression(COperator.
				bit_and, this.loperand, this.roperand);
		condition = this.sym_expression(COperator.
				not_equals, operand, this.roperand);
		constraint = this.get_constraint(condition);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(sym_expression(
					COperator.bit_xor, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		/**
		 * 	[y != 0] --> set_expr(x << y)
		 */
		SymConstraint constraint; SymStateError init_error;
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
		SymConstraint constraint; SymStateError init_error;
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
		 * 	[x != 0 || y != 0] --> set_expr(x * y)
		 **/
		SymConstraint constraint; SymStateError init_error; 
		SymbolExpression condition;
		List<SymConstraint> constraints = new ArrayList<SymConstraint>();
		
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
		SymConstraint constraint; SymStateError init_error; 
		SymbolExpression condition;
		List<SymConstraint> constraints = new ArrayList<SymConstraint>();
		
		condition = SymbolFactory.identifier(CBasicTypeImpl.bool_type, CirSetOperatorParser.AnyBoolean);
		constraints.add(get_constraint(sym_expression(COperator.not_equals, loperand, condition)));
		constraints.add(get_constraint(sym_expression(COperator.not_equals, roperand, condition)));
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
		SymConstraint constraint = this.get_constraint(Boolean.TRUE);
		SymStateError init_error;
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
		SymConstraint constraint = this.get_constraint(Boolean.TRUE);
		SymStateError init_error;
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
		SymConstraint constraint = this.get_constraint(Boolean.TRUE);
		SymStateError init_error;
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
		SymConstraint constraint = this.get_constraint(Boolean.TRUE);
		SymStateError init_error;
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
		SymConstraint constraint = this.get_constraint(Boolean.TRUE);
		SymStateError init_error;
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
		SymConstraint constraint = this.get_constraint(Boolean.TRUE);
		SymStateError init_error;
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
