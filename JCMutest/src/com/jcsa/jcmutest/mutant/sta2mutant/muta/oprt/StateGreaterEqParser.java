package com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt;

import java.util.Collection;
import java.util.HashSet;

import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateOperatorParser;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class StateGreaterEqParser extends StateOperatorParser {

	@Override
	protected boolean to_assign() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean arith_add() throws Exception {
		/**
		 * 	[M]	(x >= y ~~ x + y)
		 * 	[C]	{x >= y != x + y}
		 * 	[E]	{x >= y -> x + y}
		 * **/
		return this.parse_by_operator(COperator.arith_add);
	}

	@Override
	protected boolean arith_sub() throws Exception {
		/**
		 * 	[M]	(x >= y ~~ x - y)
		 * 	[C]	{x >= y != x - y} and {x <= y}
		 * 	[E]	{x >= y -> x - y}
		 * **/
		Collection<Object> expressions = new HashSet<Object>();
		expressions.add(this.dif_condition(COperator.arith_sub));
		expressions.add(this.sym_expression(COperator.smaller_eq, this.loperand, this.roperand));
		SymbolExpression condition = this.sym_conjunction(expressions);
		return this.parse_by_condition_and_operator(condition, COperator.arith_sub);
	}

	@Override
	protected boolean arith_mul() throws Exception {
		/**
		 * 	[M]	(x >= y ~~ x * y)
		 * 	[C]	{x >= y != x * y}
		 * 	[E]	{x >= y -> x * y}
		 * **/
		return this.parse_by_operator(COperator.arith_mul);
	}

	@Override
	protected boolean arith_div() throws Exception {
		/**
		 * 	[M]	(x >= y ~~ x / y)
		 * 	[C]	{x >= y != x / y} and {x < y}
		 * 	[E]	{x >= y -> x / y}
		 * **/
		Collection<Object> expressions = new HashSet<Object>();
		expressions.add(this.dif_condition(COperator.arith_div));
		expressions.add(this.sym_expression(COperator.smaller_tn, this.loperand, this.roperand));
		SymbolExpression condition = this.sym_conjunction(expressions);
		return this.parse_by_condition_and_operator(condition, COperator.arith_div);
	}

	@Override
	protected boolean arith_mod() throws Exception {
		/**
		 * 	[M]	(x >= y ~~ x % y)
		 * 	[C]	{x >= y != x % y} and {x <= y}
		 * 	[E]	{x >= y -> x % y}
		 * **/
		Collection<Object> expressions = new HashSet<Object>();
		expressions.add(this.dif_condition(COperator.arith_mod));
		expressions.add(this.sym_expression(COperator.smaller_eq, this.loperand, this.roperand));
		SymbolExpression condition = this.sym_conjunction(expressions);
		return this.parse_by_condition_and_operator(condition, COperator.arith_mod);
	}

	@Override
	protected boolean bitws_and() throws Exception {
		/**
		 * 	[M]	(x >= y ~~ x & y)
		 * 	[C]	{x >= y != x & y}
		 * 	[E]	{x >= y -> x & y}
		 * **/
		return this.parse_by_operator(COperator.bit_and);
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		/**
		 * 	[M]	(x >= y ~~ x | y)
		 * 	[C]	{x >= y != x | y}
		 * 	[E]	{x >= y -> x | y}
		 * **/
		return this.parse_by_operator(COperator.bit_or);
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		/**
		 * 	[M]	(x >= y ~~ x ^ y)
		 * 	[C]	{x >= y != x ^ y} and {x <= y}
		 * 	[E]	{x >= y -> x ^ y}
		 * **/
		Collection<Object> expressions = new HashSet<Object>();
		expressions.add(this.dif_condition(COperator.bit_xor));
		expressions.add(this.sym_expression(COperator.smaller_eq, this.loperand, this.roperand));
		SymbolExpression condition = this.sym_conjunction(expressions);
		return this.parse_by_condition_and_operator(condition, COperator.bit_xor);
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		/**
		 * 	[M]	(x >= y ~~ x << y)
		 * 	[C]	{x >= y != x << y}
		 * 	[E]	{x >= y -> x << y}
		 * **/
		return this.parse_by_operator(COperator.left_shift);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		/**
		 * 	[M]	(x >= y ~~ x >> y)
		 * 	[C]	{x >= y != x >> y}
		 * 	[E]	{x >= y -> x >> y}
		 * **/
		return this.parse_by_operator(COperator.righ_shift);
	}

	@Override
	protected boolean logic_and() throws Exception {
		/**
		 * 	[M]	(x >= y ~~ x && y)
		 * 	[C]	{x >= y != x && y}
		 * 	[E]	{x >= y -> x && y}
		 * **/
		return this.parse_by_operator(COperator.logic_and);
	}

	@Override
	protected boolean logic_ior() throws Exception {
		/**
		 * 	[M]	(x >= y ~~ x || y)
		 * 	[C]	{x >= y != x || y}
		 * 	[E]	{x >= y -> x || y}
		 * **/
		return this.parse_by_operator(COperator.logic_or);
	}

	@Override
	protected boolean greater_tn() throws Exception {
		/**
		 * 	[M]	(x >= y ~~ x + y)
		 * 	[C]	{x == y}
		 * 	[E]	{x >= y -> FALSE}
		 * **/
		SymbolExpression condition = this.sym_expression(
				COperator.equal_with, this.loperand, this.roperand);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

	@Override
	protected boolean greater_eq() throws Exception {
		return this.report_equivalences();
	}

	@Override
	protected boolean smaller_tn() throws Exception {
		/**
		 * 	[M]	(x >= y ~~ x < y)
		 * 	[C]	{TRUE}
		 * 	[E]	{x >= y -> x < y}
		 * **/
		SymbolExpression condition = this.sym_expression(Boolean.TRUE);
		return this.parse_by_condition_and_operator(condition, COperator.smaller_tn);
	}

	@Override
	protected boolean smaller_eq() throws Exception {
		/**
		 * 	[M]	(x >= y ~~ x <= y)
		 * 	[C]	{x != y}
		 * 	[E]	{x >= y -> x <= y}
		 * **/
		SymbolExpression condition = this.sym_expression(
				COperator.not_equals, this.loperand, this.roperand);
		return this.parse_by_condition_and_operator(condition, COperator.smaller_eq);
	}

	@Override
	protected boolean equal_with() throws Exception {
		/**
		 * 	[M]	(x >= y ~~ x == y)
		 * 	[C]	{x > y}
		 * 	[E]	{x >= y -> FALSE}
		 * **/
		SymbolExpression condition = this.sym_expression(
				COperator.greater_tn, this.loperand, this.roperand);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

	@Override
	protected boolean not_equals() throws Exception {
		/**
		 * 	[M]	(x >= y ~~ x != y)
		 * 	[C]	{x <= y}
		 * 	[E]	{x >= y -> x != y}
		 * **/
		SymbolExpression condition = this.sym_expression(
				COperator.smaller_eq, this.loperand, this.roperand);
		return this.parse_by_condition_and_operator(condition, COperator.not_equals);
	}

}
