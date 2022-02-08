package com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt;

import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirOperatorParser;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class CirArithAddParser extends CirOperatorParser {

	@Override
	protected boolean to_assign() throws Exception {
		/**
		 * [M]	{x += y --> x = y}
		 * [C]	{x != 0}
		 * [E]	{x + y --> y}
		 */
		SymbolExpression condition = this.sym_expression(COperator.
					not_equals, this.loperand, Integer.valueOf(0));
		SymbolExpression muvalue = this.sym_expression(this.roperand);
		return this.parse_by_condition_and_muvalue(condition, muvalue);
	}

	@Override
	protected boolean arith_add() throws Exception {
		/** {x + y; x + y} **/
		return this.report_equivalences();
	}

	@Override
	protected boolean arith_sub() throws Exception {
		/**
		 * 	[M]	{x + y; x - y}
		 * 	[C]	{y != 0}
		 * 	[E]	{x + y --> x - y}
		 * **/
		SymbolExpression condition = this.sym_expression(COperator.
					not_equals, this.roperand, Integer.valueOf(0));
		return this.parse_by_condition_and_operator(condition, COperator.arith_sub);
	}

	@Override
	protected boolean arith_mul() throws Exception {
		/**
		 * 	[M]	{x + y -> x * y}
		 * 	[C]	{x + y != x * y}
		 * 	[E]	{x + y -> x * y}
		 * **/
		return this.parse_by_operator(COperator.arith_mul);
	}

	@Override
	protected boolean arith_div() throws Exception {
		/**
		 * 	[M]	{x + y -> x / y}
		 * 	[C]	{x + y != x / y}
		 * 	[E]	{x + y -> x / y}
		 * **/
		return this.parse_by_operator(COperator.arith_div);
	}

	@Override
	protected boolean arith_mod() throws Exception {
		/**
		 * 	[M]	{x + y -> x % y}
		 * 	[C]	{x + y != x % y}
		 * 	[E]	{x + y -> x % y}
		 * **/
		return this.parse_by_operator(COperator.arith_mod);
	}

	@Override
	protected boolean bitws_and() throws Exception {
		/**
		 * 	[M]	{x + y -> x & y}
		 * 	[C]	{x + y != x & y}
		 * 	[E]	{x + y -> x & y}
		 * **/
		return this.parse_by_operator(COperator.bit_and);
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		/**
		 * 	[M]	{x + y -> x | y}
		 * 	[C]	{x & y != 0}
		 * 	[E]	{x + y -> x | y}
		 * **/
		SymbolExpression operand = this.sym_expression(COperator.
						bit_and, this.loperand, this.roperand);
		SymbolExpression condition = this.sym_expression(COperator.
						not_equals, operand, Integer.valueOf(0));
		return this.parse_by_condition_and_operator(condition, COperator.bit_or);
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		/**
		 * 	[M]	{x + y -> x ^ y}
		 * 	[C]	{x & y != 0}
		 * 	[E]	{x + y -> x ^ y}
		 * **/
		SymbolExpression operand = this.sym_expression(COperator.
						bit_and, this.loperand, this.roperand);
		SymbolExpression condition = this.sym_expression(COperator.
						not_equals, operand, Integer.valueOf(0));
		return this.parse_by_condition_and_operator(condition, COperator.bit_xor);
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		/**
		 * 	[M]	{x + y -> x << y}
		 * 	[C]	{y != 0}
		 * 	[E]	{x + y -> x << y}
		 * **/
		SymbolExpression condition = this.sym_expression(COperator.
					not_equals, this.roperand, Integer.valueOf(0));
		return this.parse_by_condition_and_operator(condition, COperator.left_shift);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		/**
		 * 	[M]	{x + y -> x >> y}
		 * 	[C]	{y != 0}
		 * 	[E]	{x + y -> x >> y}
		 * **/
		SymbolExpression condition = this.sym_expression(COperator.
					not_equals, this.roperand, Integer.valueOf(0));
		return this.parse_by_condition_and_operator(condition, COperator.righ_shift);
	}

	@Override
	protected boolean logic_and() throws Exception {
		/**
		 * 	[M]	{x + y -> x && y}
		 * 	[C]	{x + y != x && y}
		 * 	[E]	{x + y -> x && y}
		 * **/
		return this.parse_by_operator(COperator.logic_and);
	}

	@Override
	protected boolean logic_ior() throws Exception {
		/**
		 * 	[M]	{x + y -> x || y}
		 * 	[C]	{x + y != x || y}
		 * 	[E]	{x + y -> x || y}
		 * **/
		return this.parse_by_operator(COperator.logic_or);
	}

	@Override
	protected boolean greater_tn() throws Exception {
		/**
		 * 	[M]	{x + y -> x > y}
		 * 	[C]	{x + y != x > y}
		 * 	[E]	{x + y -> x > y}
		 * **/
		return this.parse_by_operator(COperator.greater_tn);
	}

	@Override
	protected boolean greater_eq() throws Exception {
		/**
		 * 	[M]	{x + y -> x >= y}
		 * 	[C]	{x + y != x >= y}
		 * 	[E]	{x + y -> x >= y}
		 * **/
		return this.parse_by_operator(COperator.greater_eq);
	}

	@Override
	protected boolean smaller_tn() throws Exception {
		/**
		 * 	[M]	{x + y -> x < y}
		 * 	[C]	{x + y != x < y}
		 * 	[E]	{x + y -> x < y}
		 * **/
		return this.parse_by_operator(COperator.smaller_tn);
	}

	@Override
	protected boolean smaller_eq() throws Exception {
		/**
		 * 	[M]	{x + y -> x <= y}
		 * 	[C]	{x + y != x <= y}
		 * 	[E]	{x + y -> x <= y}
		 * **/
		return this.parse_by_operator(COperator.smaller_eq);
	}

	@Override
	protected boolean equal_with() throws Exception {
		/**
		 * 	[M]	{x + y -> x == y}
		 * 	[C]	{x + y != x == y}
		 * 	[E]	{x + y -> x == y}
		 * **/
		return this.parse_by_operator(COperator.equal_with);
	}

	@Override
	protected boolean not_equals() throws Exception {
		/**
		 * 	[M]	{x + y -> x != y}
		 * 	[C]	{x + y != x != y}
		 * 	[E]	{x + y -> x != y}
		 * **/
		return this.parse_by_operator(COperator.not_equals);
	}

}
