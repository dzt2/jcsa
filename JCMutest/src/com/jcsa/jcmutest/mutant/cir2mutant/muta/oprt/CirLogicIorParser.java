package com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt;

import java.util.Collection;
import java.util.HashSet;

import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirOperatorParser;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class CirLogicIorParser extends CirOperatorParser {

	@Override
	protected boolean to_assign() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean arith_add() throws Exception {
		return this.report_equivalences();
	}

	@Override
	protected boolean arith_sub() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x - y)
		 * 	[C]	{x && y}
		 * 	[E]	{x || y -> FALSE}
		 * **/
		Collection<Object> expressions = new HashSet<Object>();
		expressions.add(this.sym_condition(this.loperand, true));
		expressions.add(this.sym_condition(this.roperand, true));
		SymbolExpression condition = this.sym_conjunction(expressions);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

	@Override
	protected boolean arith_mul() throws Exception {
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
	protected boolean arith_div() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x / y)
		 * 	[C]	{!y}				{!x && y}
		 * 	[E]	{trapping}			{FALSE}
		 * **/
		SymbolExpression condition = this.sym_condition(this.roperand, false);
		this.parse_by_condition_and_trapping(condition);
		
		Collection<Object> expressions = new HashSet<Object>();
		expressions.add(this.sym_condition(this.loperand, false));
		expressions.add(this.sym_condition(this.roperand, true));
		condition = this.sym_conjunction(expressions);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

	@Override
	protected boolean arith_mod() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x / y)
		 * 	[C]	{!y}				{y}
		 * 	[E]	{trapping}			{!x}
		 * **/
		SymbolExpression condition, muvalue;
		condition = this.sym_condition(this.roperand, false);
		this.parse_by_condition_and_trapping(condition);
		
		condition = this.sym_condition(this.roperand, true);
		muvalue = this.sym_condition(this.loperand, false);
		return this.parse_by_condition_and_muvalue(condition, muvalue);
	}

	@Override
	protected boolean bitws_and() throws Exception {
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
	protected boolean bitws_ior() throws Exception {
		return this.report_equivalences();
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x - y)
		 * 	[C]	{x && y}
		 * 	[E]	{x || y -> FALSE}
		 * **/
		Collection<Object> expressions = new HashSet<Object>();
		expressions.add(this.sym_condition(this.loperand, true));
		expressions.add(this.sym_condition(this.roperand, true));
		SymbolExpression condition = this.sym_conjunction(expressions);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x << y)
		 * 	[C]	{!x && y}
		 * 	[E]	{x || y -> FALSE}
		 * **/
		Collection<Object> expressions = new HashSet<Object>();
		expressions.add(this.sym_condition(this.loperand, false));
		expressions.add(this.sym_condition(this.roperand, true));
		SymbolExpression condition = this.sym_conjunction(expressions);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x >> y)
		 * 	[C]	{y}
		 * 	[E]	{x || y -> FALSE}
		 * **/
		SymbolExpression condition = this.sym_condition(this.roperand, true);
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
		 * 	[E]	{x || y -> !x}
		 * **/
		SymbolExpression condition = this.sym_condition(this.roperand, true);
		SymbolExpression muvalue = this.sym_condition(this.loperand, false);
		return this.parse_by_condition_and_muvalue(condition, muvalue);
	}

	@Override
	protected boolean greater_eq() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x >= y)
		 * 	[C]	{!x}
		 * 	[E]	{x || y -> !y}
		 * **/
		SymbolExpression condition = this.sym_condition(this.loperand, false);
		SymbolExpression muvalue = this.sym_condition(this.roperand, false);
		return this.parse_by_condition_and_muvalue(condition, muvalue);
	}

	@Override
	protected boolean smaller_tn() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x < y)
		 * 	[C]	{x}
		 * 	[E]	{x || y -> !y}
		 * **/
		SymbolExpression condition = this.sym_condition(this.loperand, true);
		SymbolExpression muvalue = this.sym_condition(this.roperand, false);
		return this.parse_by_condition_and_muvalue(condition, muvalue);
	}

	@Override
	protected boolean smaller_eq() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x <= y)
		 * 	[C]	{!y}
		 * 	[E]	{x || y -> !x}
		 * **/
		SymbolExpression condition = this.sym_condition(this.roperand, false);
		SymbolExpression muvalue = this.sym_condition(this.loperand, false);
		return this.parse_by_condition_and_muvalue(condition, muvalue);
	}

	@Override
	protected boolean equal_with() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x == y)
		 * 	[C]	{!x || !y}
		 * 	[E]	{x || y -> x == y}
		 * **/
		Collection<Object> expressions = new HashSet<Object>();
		expressions.add(this.sym_condition(this.loperand, false));
		expressions.add(this.sym_condition(this.roperand, false));
		SymbolExpression condition = this.sym_disjunction(expressions);
		SymbolExpression muvalue = this.sym_condition(this.expression, false);
		return this.parse_by_condition_and_muvalue(condition, muvalue);
	}

	@Override
	protected boolean not_equals() throws Exception {
		/**
		 * 	[M]	(x || y ~~ x == y)
		 * 	[C]	{x && y}
		 * 	[E]	{x || y -> FALSE}
		 * **/
		Collection<Object> expressions = new HashSet<Object>();
		expressions.add(this.sym_condition(this.loperand, true));
		expressions.add(this.sym_condition(this.roperand, true));
		SymbolExpression condition = this.sym_conjunction(expressions);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

}
