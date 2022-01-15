package com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt;

import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateOperatorParser;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class StateLogicIorParser extends StateOperatorParser {

	@Override
	protected boolean to_assign() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean arith_add() throws Exception {
		return this.logic_ior();
	}

	@Override
	protected boolean arith_sub() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x - y)
		 * 	[C]	{x && y}
		 * 	[E]	{x || y -> FALSE}
		 * **/
		SymbolExpression condition = this.sym_expression(COperator.logic_and, this.loperand, this.roperand);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

	@Override
	protected boolean arith_mul() throws Exception {
		return this.logic_and();
	}

	@Override
	protected boolean arith_div() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x / y)
		 * 	[C]	{!y}				{!x && y}
		 * 	[E]	{x || y -> traps}	{x || y -> FALSE}
		 * **/
		SymbolExpression condition = this.sym_condition(this.roperand, false);
		this.parse_by_condition_and_trapping(condition);
		
		SymbolExpression lcondition = this.sym_condition(this.loperand, false);
		SymbolExpression rcondition = this.sym_condition(this.roperand, true);
		condition = this.sym_expression(COperator.logic_and, lcondition, rcondition);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

	@Override
	protected boolean arith_mod() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x / y)
		 * 	[C]	{!y}				{x && y}
		 * 	[E]	{x || y -> traps}	{x || y -> FALSE}
		 * **/
		SymbolExpression condition = this.sym_condition(this.roperand, false);
		this.parse_by_condition_and_trapping(condition);
		
		SymbolExpression lcondition = this.sym_condition(this.loperand, true);
		SymbolExpression rcondition = this.sym_condition(this.roperand, true);
		condition = this.sym_expression(COperator.logic_and, lcondition, rcondition);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

	@Override
	protected boolean bitws_and() throws Exception {
		return this.logic_and();
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		return this.logic_ior();
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x - y)
		 * 	[C]	{x && y}
		 * 	[E]	{x || y -> FALSE}
		 * **/
		SymbolExpression condition = this.sym_expression(COperator.logic_and, this.loperand, this.roperand);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x << y)
		 * 	[C]	{!x && y}
		 * 	[E]	{x || y -> FALSE}
		 * **/
		SymbolExpression lcondition = this.sym_condition(this.loperand, false);
		SymbolExpression rcondition = this.sym_condition(this.roperand, true);
		SymbolExpression condition = this.sym_expression(COperator.logic_and, lcondition, rcondition);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x >> y)
		 * 	[C]	{y}
		 * 	[E]	{x || y -> FALSE}
		 * **/
		SymbolExpression condition = this.sym_condition(this.roperand);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

	@Override
	protected boolean logic_and() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x && y)
		 * 	[C]	{x != y}
		 * 	[E]	{x || y -> FALSE}
		 * **/
		SymbolExpression condition = this.sym_expression(
				COperator.not_equals, this.loperand, this.roperand);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

	@Override
	protected boolean logic_ior() throws Exception {
		return this.report_equivalences();
	}

	@Override
	protected boolean greater_tn() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x > y)
		 * 	[C]	{y}
		 * 	[E]	{x || y -> FALSE}
		 * **/
		SymbolExpression condition = this.sym_condition(this.roperand);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

	@Override
	protected boolean greater_eq() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x >= y)
		 * 	[C]	{!x}
		 * 	[E]	{x || y -> !(x || y)}
		 * **/
		SymbolExpression condition = this.sym_condition(this.loperand, false);
		SymbolExpression muvalue = this.sym_condition(this.expression, false);
		return this.parse_by_condition_and_muvalue(condition, muvalue);
	}

	@Override
	protected boolean smaller_tn() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x < y)
		 * 	[C]	{x}
		 * 	[E]	{x || y -> FALSE}
		 * **/
		SymbolExpression condition = this.sym_condition(this.loperand);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

	@Override
	protected boolean smaller_eq() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x <= y)
		 * 	[C]	{!y}
		 * 	[E]	{x || y -> !(x || y)}
		 * **/
		SymbolExpression condition = this.sym_condition(this.roperand, false);
		SymbolExpression muvalue = this.sym_condition(this.expression, false);
		return this.parse_by_condition_and_muvalue(condition, muvalue);
	}

	@Override
	protected boolean equal_with() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x == y)
		 * 	[C]	{!x || !y}
		 * 	[E]	{x || y -> !(x || y)}
		 * **/
		SymbolExpression lcondition = this.sym_condition(this.loperand, false);
		SymbolExpression rcondition = this.sym_condition(this.roperand, false);
		SymbolExpression condition = this.sym_expression(COperator.logic_or, lcondition, rcondition);
		SymbolExpression muvalue = this.sym_condition(this.expression, false);
		return this.parse_by_condition_and_muvalue(condition, muvalue);
	}

	@Override
	protected boolean not_equals() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x != y)
		 * 	[C]	{x && y}
		 * 	[E]	{x || y -> FALSE}
		 * **/
		SymbolExpression condition = this.sym_expression(COperator.logic_and, this.loperand, this.roperand);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

}
