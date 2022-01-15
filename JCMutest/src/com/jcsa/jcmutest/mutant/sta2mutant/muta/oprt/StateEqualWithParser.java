package com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt;

import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateOperatorParser;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class StateEqualWithParser extends StateOperatorParser {

	@Override
	protected boolean to_assign() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean arith_add() throws Exception {
		/**
		 * 	[M]	(x == y ~~ x + y)
		 * 	[C]	{x == y != x + y}
		 * 	[E]	{x == y -> x + y}
		 * **/
		return this.parse_by_operator(COperator.arith_add);
	}

	@Override
	protected boolean arith_sub() throws Exception {
		/**
		 * 	[M]	(x == y ~~ x - y)
		 * 	[C]	{TRUE}
		 * 	[E]	{x == y -> x != y}
		 * **/
		SymbolExpression condition = this.sym_expression(Boolean.TRUE);
		return this.parse_by_condition_and_operator(condition, COperator.not_equals);
	}

	@Override
	protected boolean arith_mul() throws Exception {
		/**
		 * 	[M]	(x == y ~~ x * y)
		 * 	[C]	{x == y != x * y}
		 * 	[E]	{x == y -> x * y}
		 * **/
		return this.parse_by_operator(COperator.arith_mul);
	}

	@Override
	protected boolean arith_div() throws Exception {
		/**
		 * 	[M]	(x == y ~~ x / y)
		 * 	[C]	{x == y != x / y}
		 * 	[E]	{x == y -> x / y}
		 * **/
		return this.parse_by_operator(COperator.arith_div);
	}

	@Override
	protected boolean arith_mod() throws Exception {
		/**
		 * 	[M]	(x == y ~~ x % y)
		 * 	[C]	{TRUE}
		 * 	[E]	{x == y -> x != y}
		 * **/
		SymbolExpression condition = this.sym_expression(Boolean.TRUE);
		return this.parse_by_condition_and_operator(condition, COperator.not_equals);
	}

	@Override
	protected boolean bitws_and() throws Exception {
		/**
		 * 	[M]	(x == y ~~ x & y)
		 * 	[C]	{x == y != x & y}
		 * 	[E]	{x == y -> x & y}
		 * **/
		return this.parse_by_operator(COperator.bit_and);
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		/**
		 * 	[M]	(x == y ~~ x | y)
		 * 	[C]	{x == y != x | y}
		 * 	[E]	{x == y -> x | y}
		 * **/
		return this.parse_by_operator(COperator.bit_or);
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		/**
		 * 	[M]	(x == y ~~ x ^ y)
		 * 	[C]	{TRUE}
		 * 	[E]	{x == y -> x != y}
		 * **/
		SymbolExpression condition = this.sym_expression(Boolean.TRUE);
		return this.parse_by_condition_and_operator(condition, COperator.not_equals);
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		/**
		 * 	[M]	(x == y ~~ x << y)
		 * 	[C]	{x == y != x << y}
		 * 	[E]	{x == y -> x << y}
		 * **/
		return this.parse_by_operator(COperator.left_shift);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		/**
		 * 	[M]	(x == y ~~ x >> y)
		 * 	[C]	{x == y != x >> y}
		 * 	[E]	{x == y -> x >> y}
		 * **/
		return this.parse_by_operator(COperator.righ_shift);
	}

	@Override
	protected boolean logic_and() throws Exception {
		/**
		 * 	[M]	(x == y ~~ x && y)
		 * 	[C]	{x == y != x && y}
		 * 	[E]	{x == y -> x && y}
		 * **/
		return this.parse_by_operator(COperator.logic_and);
	}

	@Override
	protected boolean logic_ior() throws Exception {
		/**
		 * 	[M]	(x == y ~~ x || y)
		 * 	[C]	{x == y != x || y}
		 * 	[E]	{x == y -> x || y}
		 * **/
		return this.parse_by_operator(COperator.logic_or);
	}

	@Override
	protected boolean greater_tn() throws Exception {
		/**
		 * 	[M]	(x == y ~~ x > y)
		 * 	[C]	{x >= y}
		 * 	[E]	{x == y -> x > y}
		 * **/
		SymbolExpression condition = this.sym_expression(
				COperator.greater_eq, this.loperand, this.roperand);
		return this.parse_by_condition_and_operator(condition, COperator.greater_tn);
	}

	@Override
	protected boolean greater_eq() throws Exception {
		/**
		 * 	[M]	(x == y ~~ x >= y)
		 * 	[C]	{x > y}
		 * 	[E]	{x == y -> TRUE}
		 * **/
		SymbolExpression condition = this.sym_expression(
				COperator.greater_tn, this.loperand, this.roperand);
		return this.parse_by_condition_and_muvalue(condition, Boolean.TRUE);
	}

	@Override
	protected boolean smaller_tn() throws Exception {
		/**
		 * 	[M]	(x == y ~~ x < y)
		 * 	[C]	{x <= y}
		 * 	[E]	{x == y -> x < y}
		 * **/
		SymbolExpression condition = this.sym_expression(
				COperator.smaller_eq, this.loperand, this.roperand);
		return this.parse_by_condition_and_operator(condition, COperator.smaller_tn);
	}

	@Override
	protected boolean smaller_eq() throws Exception {
		/**
		 * 	[M]	(x == y ~~ x <= y)
		 * 	[C]	{x < y}
		 * 	[E]	{x == y -> TRUE}
		 * **/
		SymbolExpression condition = this.sym_expression(
				COperator.smaller_tn, this.loperand, this.roperand);
		return this.parse_by_condition_and_muvalue(condition, Boolean.TRUE);
	}

	@Override
	protected boolean equal_with() throws Exception {
		return this.report_equivalences();
	}

	@Override
	protected boolean not_equals() throws Exception {
		/**
		 * 	[M]	(x == y ~~ x != y)
		 * 	[C]	{TRUE}
		 * 	[E]	{x == y -> x != y}
		 * **/
		SymbolExpression condition = this.sym_expression(Boolean.TRUE);
		return this.parse_by_condition_and_operator(condition, COperator.not_equals);
	}

}
