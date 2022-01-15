package com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt;

import java.util.Collection;
import java.util.HashSet;

import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateOperatorParser;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class StateNotEqualsParser extends StateOperatorParser {

	@Override
	protected boolean to_assign() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean arith_add() throws Exception {
		/**
		 * 	[M]	(x != y ~~ x + y)
		 * 	[C]	{x != y != x + y} and {x != 0} and {y != 0}
		 * 	[E]	{x != y -> x + y}
		 * **/
		Collection<Object> expressions = new HashSet<Object>();
		expressions.add(this.dif_condition(COperator.arith_add));
		expressions.add(this.sym_expression(COperator.not_equals, this.loperand, Integer.valueOf(0)));
		expressions.add(this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(0)));
		SymbolExpression condition = this.sym_conjunction(expressions);
		return this.parse_by_condition_and_operator(condition, COperator.arith_add);
	}

	@Override
	protected boolean arith_sub() throws Exception {
		return this.report_equivalences();
	}

	@Override
	protected boolean arith_mul() throws Exception {
		/**
		 * 	[M]	(x != y ~~ x * y)
		 * 	[C]	{x != y != x * y}
		 * 	[E]	{x != y -> x * y}
		 * **/
		return this.parse_by_operator(COperator.arith_mul);
	}

	@Override
	protected boolean arith_div() throws Exception {
		/**
		 * 	[M]	(x != y ~~ x / y)
		 * 	[C]	{x != y != x / y} and {x <= y}
		 * 	[E]	{x != y -> x / y}
		 * **/
		Collection<Object> expressions = new HashSet<Object>();
		expressions.add(this.dif_condition(COperator.arith_add));
		expressions.add(this.sym_expression(COperator.smaller_eq, this.loperand, this.roperand));
		SymbolExpression condition = this.sym_conjunction(expressions);
		return this.parse_by_condition_and_operator(condition, COperator.arith_div);
	}

	@Override
	protected boolean arith_mod() throws Exception {
		return this.report_equivalences();
	}

	@Override
	protected boolean bitws_and() throws Exception {
		/**
		 * 	[M]	(x != y ~~ x & y)
		 * 	[C]	{x != y != x & y}
		 * 	[E]	{x != y -> x & y}
		 * **/
		return this.parse_by_operator(COperator.bit_and);
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		/**
		 * 	[M]	(x != y ~~ x | y)
		 * 	[C]	{x != y != x | y} and {y != 0}
		 * 	[E]	{x != y -> x | y}
		 * **/
		Collection<Object> expressions = new HashSet<Object>();
		expressions.add(this.dif_condition(COperator.bit_or));
		expressions.add(this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(0)));
		SymbolExpression condition = this.sym_conjunction(expressions);
		return this.parse_by_condition_and_operator(condition, COperator.bit_or);
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		return this.report_equivalences();
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		/**
		 * 	[M]	(x != y ~~ x << y)
		 * 	[C]	{x != y != x << y} and {y != 0}
		 * 	[E]	{x != y -> x << y}
		 * **/
		Collection<Object> expressions = new HashSet<Object>();
		expressions.add(this.dif_condition(COperator.left_shift));
		expressions.add(this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(0)));
		SymbolExpression condition = this.sym_conjunction(expressions);
		return this.parse_by_condition_and_operator(condition, COperator.left_shift);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		/**
		 * 	[M]	(x != y ~~ x >> y)
		 * 	[C]	{x != y != x >> y} and {y != 0}
		 * 	[E]	{x != y -> x >> y}
		 * **/
		Collection<Object> expressions = new HashSet<Object>();
		expressions.add(this.dif_condition(COperator.righ_shift));
		expressions.add(this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(0)));
		SymbolExpression condition = this.sym_conjunction(expressions);
		return this.parse_by_condition_and_operator(condition, COperator.righ_shift);
	}

	@Override
	protected boolean logic_and() throws Exception {
		/**
		 * 	[M]	(x != y ~~ x && y)
		 * 	[C]	{x != y != x && y}
		 * 	[E]	{x != y -> x && y}
		 * **/
		return this.parse_by_operator(COperator.logic_and);
	}

	@Override
	protected boolean logic_ior() throws Exception {
		/**
		 * 	[M]	(x != y ~~ x || y)
		 * 	[C]	{x != y != x || y} and {y != 0}
		 * 	[E]	{x != y -> x || y}
		 * **/
		Collection<Object> expressions = new HashSet<Object>();
		expressions.add(this.dif_condition(COperator.logic_or));
		expressions.add(this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(0)));
		SymbolExpression condition = this.sym_conjunction(expressions);
		return this.parse_by_condition_and_operator(condition, COperator.logic_or);
	}

	@Override
	protected boolean greater_tn() throws Exception {
		/**
		 * 	[M]	(x != y ~~ x > y)
		 * 	[C]	{x < y}
		 * 	[E]	{x != y -> FALSE}
		 * **/
		SymbolExpression condition = this.sym_expression(
				COperator.smaller_tn, this.loperand, this.roperand);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

	@Override
	protected boolean greater_eq() throws Exception {
		/**
		 * 	[M]	(x != y ~~ x >= y)
		 * 	[C]	{x <= y}
		 * 	[E]	{x != y -> x >= y}
		 * **/
		SymbolExpression condition = this.
				sym_expression(COperator.smaller_eq, this.loperand, this.roperand);
		return this.parse_by_condition_and_operator(condition, COperator.greater_eq);
	}

	@Override
	protected boolean smaller_tn() throws Exception {
		/**
		 * 	[M]	(x != y ~~ x < y)
		 * 	[C]	{x > y}
		 * 	[E]	{x != y -> FALSE}
		 * **/
		SymbolExpression condition = this.sym_expression(
				COperator.greater_tn, this.loperand, this.roperand);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

	@Override
	protected boolean smaller_eq() throws Exception {
		/**
		 * 	[M]	(x != y ~~ x <= y)
		 * 	[C]	{x >= y}
		 * 	[E]	{x != y -> x <= y}
		 * **/
		SymbolExpression condition = this.
				sym_expression(COperator.greater_eq, this.loperand, this.roperand);
		return this.parse_by_condition_and_operator(condition, COperator.smaller_eq);
	}

	@Override
	protected boolean equal_with() throws Exception {
		SymbolExpression condition = this.sym_expression(Boolean.TRUE);
		return this.parse_by_condition_and_operator(condition, COperator.equal_with);
	}

	@Override
	protected boolean not_equals() throws Exception {
		return this.report_equivalences();
	}

}
