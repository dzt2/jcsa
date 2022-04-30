package com.jcsa.jcmutest.mutant.ctx2mutant.oprt;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class CirAssignIorMutationParser extends CirOperatorMutationParser {

	@Override
	protected boolean arith_add() throws Exception {
		/** (x |= y; x += y) :: (x | y != x + y; x != 0; y != 0; x & y != 0) --> (x += y) **/
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.neq_expression(this.get_loperand(), Integer.valueOf(0)));
		conditions.add(this.neq_expression(this.get_roperand(), Integer.valueOf(0)));
		SymbolExpression operand = this.sym_expression(COperator.bit_and, this.get_loperand(), this.get_roperand());
		conditions.add(this.neq_expression(operand, Integer.valueOf(0)));
		return this.parse_by_condition_and_operator(this.sym_conjunctions(conditions), COperator.arith_add_assign);
	}

	@Override
	protected boolean arith_sub() throws Exception {
		/** (x |= y; x -= y) :: (x | y != x - y; y != 0) --> (x -= y) **/
		List<Object> conditions = new ArrayList<Object>(); SymbolExpression ovalue, mvalue;
		conditions.add(this.neq_expression(this.get_roperand(), Integer.valueOf(0)));
		ovalue = this.sym_expression(COperator.bit_or, this.get_loperand(), this.get_roperand());
		mvalue = this.sym_expression(COperator.arith_sub, this.get_loperand(), this.get_roperand());
		conditions.add(this.neq_expression(ovalue, mvalue));
		return this.parse_by_condition_and_operator(this.sym_conjunctions(conditions), COperator.arith_sub_assign);
	}

	@Override
	protected boolean arith_mul() throws Exception {
		SymbolExpression ovalue = this.sym_expression(COperator.bit_or, this.get_loperand(), this.get_roperand());
		SymbolExpression mvalue = this.sym_expression(COperator.arith_mul, this.get_loperand(), this.get_roperand());
		return this.parse_by_condition_and_operator(this.neq_expression(ovalue, mvalue), COperator.arith_mul_assign);
	}

	@Override
	protected boolean arith_div() throws Exception {
		SymbolExpression ovalue = this.sym_expression(COperator.bit_or, this.get_loperand(), this.get_roperand());
		SymbolExpression mvalue = this.sym_expression(COperator.arith_div, this.get_loperand(), this.get_roperand());
		return this.parse_by_condition_and_operator(this.neq_expression(ovalue, mvalue), COperator.arith_div_assign);
	}

	@Override
	protected boolean arith_mod() throws Exception {
		SymbolExpression ovalue = this.sym_expression(COperator.bit_or, this.get_loperand(), this.get_roperand());
		SymbolExpression mvalue = this.sym_expression(COperator.arith_mod, this.get_loperand(), this.get_roperand());
		return this.parse_by_condition_and_operator(this.neq_expression(ovalue, mvalue), COperator.arith_mod_assign);
	}

	@Override
	protected boolean bitws_and() throws Exception {
		/** (x |= y; x &= y) :: (x | y != x & y; x != y) --> (x &= y) **/
		return this.parse_by_condition_and_operator(this.neq_expression(
				this.get_loperand(), this.get_roperand()), COperator.bit_and_assign);
	}

	@Override
	protected boolean bitws_ior() throws Exception { return this.report_equivalent_mutation(); }

	@Override
	protected boolean bitws_xor() throws Exception {
		SymbolExpression ovalue = this.sym_expression(COperator.bit_or, this.get_loperand(), this.get_roperand());
		SymbolExpression mvalue = this.sym_expression(COperator.bit_xor, this.get_loperand(), this.get_roperand());
		return this.parse_by_condition_and_operator(this.neq_expression(ovalue, mvalue), COperator.bit_xor_assign);
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.neq_expression(this.get_roperand(), Integer.valueOf(0)));
		SymbolExpression ovalue = this.sym_expression(COperator.bit_or, this.get_loperand(), this.get_roperand());
		SymbolExpression mvalue = this.sym_expression(COperator.left_shift, this.get_loperand(), this.get_roperand());
		conditions.add(this.neq_expression(ovalue, mvalue));
		return this.parse_by_condition_and_operator(this.sym_conjunctions(conditions), COperator.left_shift_assign);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.neq_expression(this.get_roperand(), Integer.valueOf(0)));
		SymbolExpression ovalue = this.sym_expression(COperator.bit_or, this.get_loperand(), this.get_roperand());
		SymbolExpression mvalue = this.sym_expression(COperator.righ_shift, this.get_loperand(), this.get_roperand());
		conditions.add(this.neq_expression(ovalue, mvalue));
		return this.parse_by_condition_and_operator(this.sym_conjunctions(conditions), COperator.righ_shift_assign);
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
		/** (x |= y; x = y) :: (x | y != y; x != 0; y != ~0) --> (x = y) **/
		List<Object> conditions = new ArrayList<Object>(); SymbolExpression ovalue, mvalue;
		conditions.add(this.neq_expression(this.get_loperand(), Integer.valueOf(0)));
		conditions.add(this.neq_expression(this.get_roperand(), Integer.valueOf(~0)));
		ovalue = this.sym_expression(COperator.bit_or, this.get_loperand(), this.get_roperand());
		mvalue = this.sym_expression(this.get_roperand());
		conditions.add(this.neq_expression(ovalue, mvalue));
		return this.parse_by_condition_and_operator(this.sym_conjunctions(conditions), COperator.assign);
	}

}
