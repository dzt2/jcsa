package com.jcsa.jcmutest.mutant.ctx2mutant.oprt;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class CirAssignAddMutationParser extends CirOperatorMutationParser {

	@Override
	protected boolean arith_add() throws Exception { return this.report_equivalent_mutation(); }

	@Override
	protected boolean arith_sub() throws Exception {
		/** (x += y; x -= y) :: (y != 0) --> (x -= y) **/
		SymbolExpression condition = this.neq_expression(this.get_roperand(), Integer.valueOf(0));
		return this.parse_by_condition_and_operator(condition, COperator.arith_sub_assign);
	}

	@Override
	protected boolean arith_mul() throws Exception {
		/** (x += y; x *= y) :: (x + y != x * y) --> (x *= y) **/
		SymbolExpression loperand = this.sym_expression(COperator.arith_add, this.get_loperand(), this.get_roperand());
		SymbolExpression roperand = this.sym_expression(COperator.arith_mul, this.get_loperand(), this.get_roperand());
		return this.parse_by_condition_and_operator(this.neq_expression(loperand, roperand), COperator.arith_mul_assign);
	}

	@Override
	protected boolean arith_div() throws Exception {
		/** (x += y; x /= y) :: (x + y != x / y) --> (x /= y) **/
		SymbolExpression loperand = this.sym_expression(COperator.arith_add, this.get_loperand(), this.get_roperand());
		SymbolExpression roperand = this.sym_expression(COperator.arith_div, this.get_loperand(), this.get_roperand());
		return this.parse_by_condition_and_operator(this.neq_expression(loperand, roperand), COperator.arith_div_assign);
	}

	@Override
	protected boolean arith_mod() throws Exception {
		/** (x += y; x %= y) :: (x + y != x % y) --> (x %= y) **/
		SymbolExpression loperand = this.sym_expression(COperator.arith_add, this.get_loperand(), this.get_roperand());
		SymbolExpression roperand = this.sym_expression(COperator.arith_mod, this.get_loperand(), this.get_roperand());
		return this.parse_by_condition_and_operator(this.neq_expression(loperand, roperand), COperator.arith_mod_assign);
	}

	@Override
	protected boolean bitws_and() throws Exception {
		SymbolExpression loperand = this.sym_expression(COperator.arith_add, this.get_loperand(), this.get_roperand());
		SymbolExpression roperand = this.sym_expression(COperator.bit_and, this.get_loperand(), this.get_roperand());
		return this.parse_by_condition_and_operator(this.neq_expression(loperand, roperand), COperator.bit_and_assign);
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		/** (x += y; x |= y) :: (x != 0; y != 0; x & y != 0) --> (x |= y) **/
		List<Object> conditions = new ArrayList<Object>(); SymbolExpression operand;
		conditions.add(this.neq_expression(this.get_loperand(), Integer.valueOf(0)));
		conditions.add(this.neq_expression(this.get_roperand(), Integer.valueOf(0)));
		operand = this.sym_expression(COperator.bit_and, this.get_loperand(), this.get_roperand());
		conditions.add(this.neq_expression(operand, Integer.valueOf(0)));
		return this.parse_by_condition_and_operator(this.sym_conjunctions(conditions), COperator.bit_or_assign);
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		/** (x += y; x ^= y) :: (x != 0; y != 0; x & y != 0) --> (x ^= y) **/
		List<Object> conditions = new ArrayList<Object>(); SymbolExpression operand;
		conditions.add(this.neq_expression(this.get_loperand(), Integer.valueOf(0)));
		conditions.add(this.neq_expression(this.get_roperand(), Integer.valueOf(0)));
		operand = this.sym_expression(COperator.bit_and, this.get_loperand(), this.get_roperand());
		conditions.add(this.neq_expression(operand, Integer.valueOf(0)));
		return this.parse_by_condition_and_operator(this.sym_conjunctions(conditions), COperator.bit_xor_assign);
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		SymbolExpression loperand = this.sym_expression(COperator.arith_add, this.get_loperand(), this.get_roperand());
		SymbolExpression roperand = this.sym_expression(COperator.left_shift, this.get_loperand(), this.get_roperand());
		return this.parse_by_condition_and_operator(this.neq_expression(loperand, roperand), COperator.left_shift_assign);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		SymbolExpression loperand = this.sym_expression(COperator.arith_add, this.get_loperand(), this.get_roperand());
		SymbolExpression roperand = this.sym_expression(COperator.righ_shift, this.get_loperand(), this.get_roperand());
		return this.parse_by_condition_and_operator(this.neq_expression(loperand, roperand), COperator.righ_shift_assign);
	}

	@Override
	protected boolean logic_and() throws Exception { return this.report_unsupport_exception(); }

	@Override
	protected boolean logic_ior() throws Exception { return this.report_unsupport_exception(); }

	@Override
	protected boolean greater_tn() throws Exception { return this.report_unsupport_exception(); }

	@Override
	protected boolean greater_eq() throws Exception { return this.report_unsupport_exception(); }

	@Override
	protected boolean smaller_tn() throws Exception { return this.report_unsupport_exception(); }

	@Override
	protected boolean smaller_eq() throws Exception { return this.report_unsupport_exception(); }

	@Override
	protected boolean equal_with() throws Exception { return this.report_unsupport_exception(); }

	@Override
	protected boolean not_equals() throws Exception { return this.report_unsupport_exception(); }

	@Override
	protected boolean to_assign() throws Exception { 
		/** (x += y; x = y) :: (x != 0) --> (x = y) **/
		SymbolExpression condition = 
					this.neq_expression(this.get_loperand(), Integer.valueOf(0));
		return this.parse_by_condition_and_operator(condition, COperator.assign);
	}

}
