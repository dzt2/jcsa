package com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.base.SymCondition;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirSetOperatorParser;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class CirSetArithMulParser extends CirSetOperatorParser {
	
	@Override
	protected boolean to_assign() throws Exception {
		SymCondition constraint; SymCondition init_error;
		constraint = this.get_constraint(this.sym_expression(
				COperator.not_equals, this.loperand, Integer.valueOf(1)));
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
		 * 	[x != 0 || y != 0] --> set_expr(x + y)
		 */
		SymCondition constraint; SymCondition init_error; 
		SymbolExpression condition;
		List<SymCondition> constraints = new ArrayList<SymCondition>();
		
		condition = this.sym_expression(COperator.not_equals, this.loperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		condition = this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		constraint = this.disjunct(constraints);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = set_expression(sym_expression(COperator.arith_add, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_sub() throws Exception {
		/**
		 * 	[x != 0 || y != 0] --> set_expr(x - y)
		 */
		SymCondition constraint; SymCondition init_error; 
		SymbolExpression condition;
		List<SymCondition> constraints = new ArrayList<SymCondition>();
		
		condition = this.sym_expression(COperator.not_equals, this.loperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		condition = this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		constraint = this.disjunct(constraints);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = set_expression(sym_expression(COperator.arith_sub, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_mul() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean arith_div() throws Exception {
		/**
		 * 	[y == 0] --> trap_on_statement()
		 * 	[x != 0 && y != 1 && y != -1] --> set_expr(x / y)
		 */
		SymCondition constraint; SymCondition init_error; 
		SymbolExpression condition;
		List<SymCondition> constraints = new ArrayList<SymCondition>();
		
		if(this.compare_or_mutate) {
			condition = this.sym_expression(COperator.not_equals, this.loperand, Integer.valueOf(0));
			constraints.add(this.get_constraint(condition));
			condition = this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(1));
			constraints.add(this.get_constraint(condition));
			condition = this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(-1));
			constraints.add(this.get_constraint(condition));
			constraint = this.conjunct(constraints);
			
			init_error = this.trap_statement();
			return this.add_infection(constraint, init_error);
		}
		else {
			condition = this.sym_expression(COperator.equal_with, this.roperand, Integer.valueOf(0));
			constraint = this.get_constraint(condition);
			init_error = this.trap_statement();
			this.add_infection(constraint, init_error);
			
			condition = this.sym_expression(COperator.not_equals, this.loperand, Integer.valueOf(0));
			constraints.add(this.get_constraint(condition));
			condition = this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(1));
			constraints.add(this.get_constraint(condition));
			condition = this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(-1));
			constraints.add(this.get_constraint(condition));
			constraint = this.conjunct(constraints);
			init_error = this.set_expression(this.sym_expression(COperator.arith_div, loperand, roperand));
			return this.add_infection(constraint, init_error);
		}
	}

	@Override
	protected boolean arith_mod() throws Exception {
		/**
		 * 	[y == 0] --> trap_stmt()
		 * 	[x != 0] --> set_expr(x % y)
		 */
		SymCondition constraint; SymCondition init_error; 
		SymbolExpression condition;
		if(this.compare_or_mutate) {
			condition = this.sym_expression(COperator.not_equals, this.loperand, Integer.valueOf(0));
			constraint = this.get_constraint(condition);
			init_error = this.trap_statement();
			return this.add_infection(constraint, init_error);
		}
		else {
			condition = this.sym_expression(COperator.equal_with, this.roperand, Integer.valueOf(0));
			constraint = this.get_constraint(condition);
			init_error = this.trap_statement();
			this.add_infection(constraint, init_error);
			
			condition = this.sym_expression(COperator.not_equals, this.loperand, Integer.valueOf(0));
			constraint = this.get_constraint(condition);
			init_error = this.set_expression(this.sym_expression(COperator.arith_mod, loperand, roperand));
			return this.add_infection(constraint, init_error);
		}
	}

	@Override
	protected boolean bitws_and() throws Exception {
		/**
		 * 	[x != 0 && y != 0] --> set_expr(x & y)
		 */
		SymCondition constraint; SymCondition init_error; 
		SymbolExpression condition;
		List<SymCondition> constraints = new ArrayList<SymCondition>();
		
		condition = this.sym_expression(COperator.not_equals, this.loperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		condition = this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		constraint = this.conjunct(constraints);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = set_expression(sym_expression(COperator.bit_and, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		/**
		 * 	[x != 0 || y != 0] --> set_expr(x | y)
		 */
		SymCondition constraint; SymCondition init_error; 
		SymbolExpression condition;
		List<SymCondition> constraints = new ArrayList<SymCondition>();
		
		condition = this.sym_expression(COperator.not_equals, this.loperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		condition = this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		constraint = this.disjunct(constraints);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = set_expression(sym_expression(COperator.bit_or, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		/**
		 * 	[x != 0 || y != 0] --> set_expr(x ^ y)
		 */
		SymCondition constraint; SymCondition init_error; 
		SymbolExpression condition;
		List<SymCondition> constraints = new ArrayList<SymCondition>();
		
		condition = this.sym_expression(COperator.not_equals, this.loperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		condition = this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		constraint = this.disjunct(constraints);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = set_expression(sym_expression(COperator.bit_xor, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		/**
		 * [x != 0] --> set_expr(x << y)
		 */
		SymCondition constraint; SymCondition init_error;
		constraint = get_constraint(sym_expression(COperator.
				not_equals, this.loperand, Integer.valueOf(0)));
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
		 * [x != 0] --> set_expr(x >> y)
		 */
		SymCondition constraint; SymCondition init_error;
		constraint = get_constraint(sym_expression(COperator.
				not_equals, this.loperand, Integer.valueOf(0)));
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
		 * 	[x != AnyBool || y != AnyBool]
		 */
		SymCondition constraint; SymCondition init_error;
		SymbolExpression operand;
		List<SymCondition> constraints = new ArrayList<SymCondition>();
		
		operand = SymbolFactory.identifier(CBasicTypeImpl.bool_type, CirSetOperatorParser.AnyBoolean);
		constraints.add(get_constraint(sym_expression(COperator.not_equals, loperand, operand)));
		constraints.add(get_constraint(sym_expression(COperator.not_equals, roperand, operand)));
		constraint = this.disjunct(constraints);
		
		if(this.compare_or_mutate) {
			init_error = this.trap_statement();
		}
		else {
			init_error = set_expression(sym_expression(COperator.logic_and, loperand, roperand));
		}
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean logic_ior() throws Exception {
		/**
		 * 	[x != 0 || y != 0] --> set_expr(x || y)
		 */
		SymCondition constraint; SymCondition init_error;
		SymbolExpression condition;
		List<SymCondition> constraints = new ArrayList<SymCondition>();
		
		condition = this.sym_expression(COperator.not_equals, this.loperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
		condition = this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(0));
		constraints.add(this.get_constraint(condition));
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
		SymCondition constraint = this.get_constraint(Boolean.TRUE);
		SymCondition init_error;
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
		SymCondition constraint = this.get_constraint(Boolean.TRUE);
		SymCondition init_error;
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
		SymCondition constraint = this.get_constraint(Boolean.TRUE);
		SymCondition init_error;
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
		SymCondition constraint = this.get_constraint(Boolean.TRUE);
		SymCondition init_error;
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
		SymCondition constraint = this.get_constraint(Boolean.TRUE);
		SymCondition init_error;
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
		SymCondition constraint = this.get_constraint(Boolean.TRUE);
		SymCondition init_error;
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
