package com.jcsa.jcmutest.mutant.ctx2mutant.muta.oprt;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class CirBitwsXorMutationParser extends CirOperatorMutationParser {

	@Override
	protected boolean to_assign() throws Exception {
		/** (x ^= y; x = y) :: (x != 0) --> (x = y) **/
		SymbolExpression condition = this.sym_expression(COperator.
				not_equals, this.get_loperand(), Integer.valueOf(0));
		return this.parse_by_condition_and_operator(condition, COperator.assign);
	}

	@Override
	protected boolean arith_add() throws Exception {
		/** (x ^ y; x + y) :: (x != 0; y != 0; dif_cond) :: (x + y) **/
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.sym_expression(COperator.not_equals, this.get_loperand(), Integer.valueOf(0)));
		conditions.add(this.sym_expression(COperator.not_equals, this.get_roperand(), Integer.valueOf(0)));
		conditions.add(this.dif_condition(this.sym_expression(COperator.arith_add, this.get_loperand(), this.get_roperand())));
		return this.parse_by_condition_and_operator(this.sym_conjunction(conditions), COperator.arith_add);
	}

	@Override
	protected boolean arith_sub() throws Exception {
		/** (x ^ y; x - y) :: (x != y; y != 0) --> (x - y) **/
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.sym_expression(COperator.not_equals, this.get_loperand(), this.get_roperand()));
		conditions.add(this.sym_expression(COperator.not_equals, this.get_roperand(), Integer.valueOf(0)));
		conditions.add(this.dif_condition(this.sym_expression(COperator.arith_sub, this.get_loperand(), this.get_roperand())));
		return this.parse_by_condition_and_operator(this.sym_conjunction(conditions), COperator.arith_sub);
	}

	@Override
	protected boolean arith_mul() throws Exception {
		return this.parse_by_operator(COperator.arith_mul);
	}

	@Override
	protected boolean arith_div() throws Exception {
		return this.parse_by_operator(COperator.arith_div);
	}

	@Override
	protected boolean arith_mod() throws Exception {
		/** (x ^ y; x % y) :: (x != y; dif_cond) --> (x % y) **/
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.sym_expression(COperator.not_equals, this.get_loperand(), this.get_roperand()));
		conditions.add(this.dif_condition(this.sym_expression(COperator.arith_mod, this.get_loperand(), this.get_roperand())));
		return this.parse_by_condition_and_operator(this.sym_conjunction(conditions), COperator.arith_mod);
	}

	@Override
	protected boolean bitws_and() throws Exception {
		/** (x ^ y; x & y) :: (dif_cond) **/
		return this.parse_by_operator(COperator.bit_and);
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		/** (x ^ y; x | y) :: (x != 0; y != 0; x & y != 0) --> (x | y) **/
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.sym_expression(COperator.not_equals, this.get_loperand(), Integer.valueOf(0)));
		conditions.add(this.sym_expression(COperator.not_equals, this.get_roperand(), Integer.valueOf(0)));
		SymbolExpression operand = this.sym_expression(COperator.bit_and, this.get_loperand(), this.get_roperand());
		conditions.add(this.sym_expression(COperator.not_equals, operand, Integer.valueOf(0)));
		return this.parse_by_condition_and_operator(this.sym_conjunction(conditions), COperator.bit_or);
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		return this.parse_by_equivalence();
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		/** (x ^ y; x << y) :: (y != 0; dif_cond) --> (x << y) **/
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.sym_expression(COperator.not_equals, this.get_roperand(), Integer.valueOf(0)));
		conditions.add(this.dif_condition(this.sym_expression(COperator.left_shift, this.get_loperand(), this.get_roperand())));
		return this.parse_by_condition_and_operator(this.sym_conjunction(conditions), COperator.left_shift);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		/** (x ^ y; x >> y) :: (y != 0; dif_cond) --> (x >> y) **/
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.sym_expression(COperator.not_equals, this.get_roperand(), Integer.valueOf(0)));
		conditions.add(this.dif_condition(this.sym_expression(COperator.righ_shift, this.get_loperand(), this.get_roperand())));
		return this.parse_by_condition_and_operator(this.sym_conjunction(conditions), COperator.righ_shift);
	}

	@Override
	protected boolean logic_and() throws Exception {
		return this.parse_by_operator(COperator.logic_and);
	}

	@Override
	protected boolean logic_ior() throws Exception {
		return this.parse_by_operator(COperator.logic_or);
	}

	@Override
	protected boolean greater_tn() throws Exception {
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.sym_expression(COperator.not_equals, this.get_loperand(), this.get_roperand()));
		conditions.add(this.dif_condition(this.sym_expression(COperator.greater_tn, this.get_loperand(), this.get_roperand())));
		return this.parse_by_condition_and_operator(this.sym_conjunction(conditions), COperator.greater_tn);
	}

	@Override
	protected boolean greater_eq() throws Exception {
		return this.parse_by_operator(COperator.greater_eq);
	}

	@Override
	protected boolean smaller_tn() throws Exception {
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.sym_expression(COperator.not_equals, this.get_loperand(), this.get_roperand()));
		conditions.add(this.dif_condition(this.sym_expression(COperator.smaller_tn, this.get_loperand(), this.get_roperand())));
		return this.parse_by_condition_and_operator(this.sym_conjunction(conditions), COperator.smaller_tn);
	}

	@Override
	protected boolean smaller_eq() throws Exception {
		return this.parse_by_operator(COperator.smaller_eq);
	}

	@Override
	protected boolean equal_with() throws Exception {
		return this.parse_by_operator(COperator.equal_with);
	}

	@Override
	protected boolean not_equals() throws Exception {
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.sym_expression(COperator.not_equals, this.get_loperand(), this.get_roperand()));
		conditions.add(this.dif_condition(this.sym_expression(COperator.not_equals, this.get_loperand(), this.get_roperand())));
		return this.parse_by_condition_and_operator(this.sym_conjunction(conditions), COperator.not_equals);
	}

}
