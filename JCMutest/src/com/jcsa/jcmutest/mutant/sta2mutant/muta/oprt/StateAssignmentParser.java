package com.jcsa.jcmutest.mutant.sta2mutant.muta.oprt;

import java.util.Collection;
import java.util.HashSet;

import com.jcsa.jcmutest.mutant.sta2mutant.muta.StateOperatorParser;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class StateAssignmentParser extends StateOperatorParser {

	@Override
	protected boolean to_assign() throws Exception {
		return this.report_equivalences();
	}

	@Override
	protected boolean arith_add() throws Exception {
		/**
		 * 	[M]	(x = y ~~ x += y)
		 * 	[C]	{x != 0}
		 * 	[E]	{x = y -> x + y}
		 * **/
		SymbolExpression condition = this.sym_expression(
				COperator.not_equals, this.loperand, Integer.valueOf(0));
		SymbolExpression muvalue = this.sym_expression(
				COperator.arith_add, this.loperand, this.roperand);
		return this.parse_by_condition_and_muvalue(condition, muvalue);
	}

	@Override
	protected boolean arith_sub() throws Exception {
		/**
		 * 	[M]	(x = y ~~ x -= y)
		 * 	[C]	{y != x - y}
		 * 	[E]	{x = y -> x - y}
		 * **/
		return this.parse_by_operator(COperator.arith_sub);
	}

	@Override
	protected boolean arith_mul() throws Exception {
		/**
		 * 	[M]	(x = y ~~ x *= y)
		 * 	[C]	{y != x * y} and {x != 1} and {y != 0}
		 * 	[E]	{x = y -> x * y}
		 * **/
		Collection<Object> expressions = new HashSet<Object>();
		expressions.add(this.dif_condition(COperator.arith_mul));
		expressions.add(this.sym_expression(COperator.not_equals, this.loperand, Integer.valueOf(1)));
		expressions.add(this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(0)));
		SymbolExpression condition = this.sym_conjunction(expressions);
		return this.parse_by_condition_and_operator(condition, COperator.arith_mul);
	}

	@Override
	protected boolean arith_div() throws Exception {
		/**
		 * 	[M]	(x = y ~~ x /= y)
		 * 	[C]	{y != x / y} 
		 * 	[E]	{x = y -> x / y}
		 * **/
		return this.parse_by_operator(COperator.arith_div);
	}

	@Override
	protected boolean arith_mod() throws Exception {
		/**
		 * 	[M]	(x = y ~~ x %= y)
		 * 	[C]	{y != x % y} 
		 * 	[E]	{x = y -> x % y}
		 * **/
		return this.parse_by_operator(COperator.arith_mod);
	}

	@Override
	protected boolean bitws_and() throws Exception {
		/**
		 * 	[M]	(x = y ~~ x &= y)
		 * 	[C]	{y != x & y} and {x != ~0} and {y != 0}
		 * 	[E]	{x = y -> x & y}
		 * **/
		Collection<Object> expressions = new HashSet<Object>();
		expressions.add(this.dif_condition(COperator.bit_and));
		expressions.add(this.sym_expression(COperator.not_equals, this.loperand, Integer.valueOf(~0)));
		expressions.add(this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(0)));
		SymbolExpression condition = this.sym_conjunction(expressions);
		return this.parse_by_condition_and_operator(condition, COperator.bit_and);
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		/**
		 * 	[M]	(x = y ~~ x |= y)
		 * 	[C]	{y != x | y} and {x != 0} and {y != ~0}
		 * 	[E]	{x = y -> x | y}
		 * **/
		Collection<Object> expressions = new HashSet<Object>();
		expressions.add(this.dif_condition(COperator.bit_or));
		expressions.add(this.sym_expression(COperator.not_equals, this.loperand, Integer.valueOf(0)));
		expressions.add(this.sym_expression(COperator.not_equals, this.roperand, Integer.valueOf(~0)));
		SymbolExpression condition = this.sym_conjunction(expressions);
		return this.parse_by_condition_and_operator(condition, COperator.bit_or);
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		/**
		 * 	[M]	(x = y ~~ x ^= y)
		 * 	[C]	{y != x ^ y} and {x != 0}
		 * 	[E]	{x = y -> x ^ y}
		 * **/
		Collection<Object> expressions = new HashSet<Object>();
		expressions.add(this.dif_condition(COperator.bit_xor));
		expressions.add(this.sym_expression(COperator.not_equals, this.loperand, Integer.valueOf(0)));
		SymbolExpression condition = this.sym_conjunction(expressions);
		return this.parse_by_condition_and_operator(condition, COperator.bit_xor);
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		/**
		 * 	[M]	(x = y ~~ x <<= y)
		 * 	[C]	{y != x << y}
		 * 	[E]	{x = y -> x << y}
		 * **/
		return this.parse_by_operator(COperator.left_shift);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		/**
		 * 	[M]	(x = y ~~ x >>= y)
		 * 	[C]	{y != x >> y}
		 * 	[E]	{x = y -> x >> y}
		 * **/
		return this.parse_by_operator(COperator.righ_shift);
	}

	@Override
	protected boolean logic_and() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean logic_ior() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean greater_tn() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean greater_eq() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean smaller_tn() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean smaller_eq() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean equal_with() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean not_equals() throws Exception {
		return this.unsupport_exception();
	}

}
