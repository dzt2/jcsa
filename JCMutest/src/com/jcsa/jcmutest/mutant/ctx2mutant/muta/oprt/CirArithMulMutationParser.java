package com.jcsa.jcmutest.mutant.ctx2mutant.muta.oprt;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.lexical.COperator;

public class CirArithMulMutationParser extends CirOperatorMutationParser {

	@Override
	protected boolean arith_add() throws Exception { return this.parse_by_operator(COperator.arith_add); }

	@Override
	protected boolean arith_sub() throws Exception { return this.parse_by_operator(COperator.arith_sub); }

	@Override
	protected boolean arith_mul() throws Exception { return this.report_equivalent_mutation(); }

	@Override
	protected boolean arith_div() throws Exception {
		/** (x * y; x / y) :: (x != 0; y != 1; y != -1) --> (x / y) **/
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.neq_expression(this.get_loperand(), Integer.valueOf(0)));
		conditions.add(this.neq_expression(this.get_roperand(), Integer.valueOf(1)));
		conditions.add(this.neq_expression(this.get_roperand(), Integer.valueOf(-1)));
		return this.parse_by_condition_and_operator(this.sym_conjunctions(conditions), COperator.arith_div);
	}

	@Override
	protected boolean arith_mod() throws Exception {
		/** (x * y; x % y) :: (x != 0; dif_cond) --> (x % y) **/
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.neq_expression(this.get_loperand(), Integer.valueOf(0)));
		conditions.add(this.dif_condition(this.sym_expression(COperator.arith_mod, this.get_loperand(), this.get_roperand())));
		return this.parse_by_condition_and_operator(this.sym_conjunctions(conditions), COperator.arith_mod);
	}

	@Override
	protected boolean bitws_and() throws Exception {
		/** (x * y; x & y) :: (x != 0; y != 0; dif_cond) --> (x & y) **/
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.neq_expression(this.get_loperand(), Integer.valueOf(0)));
		conditions.add(this.neq_expression(this.get_roperand(), Integer.valueOf(0)));
		conditions.add(this.dif_condition(this.sym_expression(COperator.bit_and, this.get_loperand(), this.get_roperand())));
		return this.parse_by_condition_and_operator(this.sym_conjunctions(conditions), COperator.bit_and);
	}

	@Override
	protected boolean bitws_ior() throws Exception { return this.parse_by_operator(COperator.bit_or); }

	@Override
	protected boolean bitws_xor() throws Exception { return this.parse_by_operator(COperator.bit_xor); }

	@Override
	protected boolean bitws_lsh() throws Exception {
		/** (x * y; x << y) :: (x != 0; dif_cond) --> (x << y) **/
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.neq_expression(this.get_loperand(), Integer.valueOf(0)));
		conditions.add(this.dif_condition(this.sym_expression(COperator.left_shift, this.get_loperand(), this.get_roperand())));
		return this.parse_by_condition_and_operator(this.sym_conjunctions(conditions), COperator.left_shift);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		/** (x * y; x >> y) :: (x != 0; dif_cond) --> (x >> y) **/
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.neq_expression(this.get_loperand(), Integer.valueOf(0)));
		conditions.add(this.dif_condition(this.sym_expression(COperator.righ_shift, this.get_loperand(), this.get_roperand())));
		return this.parse_by_condition_and_operator(this.sym_conjunctions(conditions), COperator.righ_shift);
	}

	@Override
	protected boolean logic_and() throws Exception {
		/** (x * y; x & y) :: (x != 0; y != 0; dif_cond) --> (x & y) **/
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.neq_expression(this.get_loperand(), Integer.valueOf(0)));
		conditions.add(this.neq_expression(this.get_roperand(), Integer.valueOf(0)));
		conditions.add(this.dif_condition(this.sym_expression(COperator.logic_and, this.get_loperand(), this.get_roperand())));
		return this.parse_by_condition_and_operator(this.sym_conjunctions(conditions), COperator.logic_and);
	}

	@Override
	protected boolean logic_ior() throws Exception { return this.parse_by_operator(COperator.logic_or); }

	@Override
	protected boolean greater_tn() throws Exception { return this.parse_by_operator(COperator.greater_tn); }

	@Override
	protected boolean greater_eq() throws Exception { return this.parse_by_operator(COperator.greater_eq); }

	@Override
	protected boolean smaller_tn() throws Exception { return this.parse_by_operator(COperator.smaller_tn); }

	@Override
	protected boolean smaller_eq() throws Exception { return this.parse_by_operator(COperator.smaller_eq); }

	@Override
	protected boolean equal_with() throws Exception { return this.parse_by_operator(COperator.equal_with); }

	@Override
	protected boolean not_equals() throws Exception { return this.parse_by_operator(COperator.not_equals); }

	@Override
	protected boolean to_assign() throws Exception { return this.report_unsupport_exception(); }

}
