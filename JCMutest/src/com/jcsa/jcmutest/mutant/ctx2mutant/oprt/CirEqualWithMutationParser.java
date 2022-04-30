package com.jcsa.jcmutest.mutant.ctx2mutant.oprt;

import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class CirEqualWithMutationParser extends CirOperatorMutationParser {

	@Override
	protected boolean arith_add() throws Exception { return this.parse_by_operator(COperator.arith_add); }

	@Override
	protected boolean arith_sub() throws Exception { return this.not_equals(); }

	@Override
	protected boolean arith_mul() throws Exception { return this.parse_by_operator(COperator.arith_mul); }

	@Override
	protected boolean arith_div() throws Exception { return this.parse_by_operator(COperator.arith_div); }

	@Override
	protected boolean arith_mod() throws Exception { return this.parse_by_operator(COperator.arith_mod); }

	@Override
	protected boolean bitws_and() throws Exception { return this.parse_by_operator(COperator.bit_and); }

	@Override
	protected boolean bitws_ior() throws Exception { return this.parse_by_operator(COperator.bit_or); }

	@Override
	protected boolean bitws_xor() throws Exception { return this.not_equals(); }

	@Override
	protected boolean bitws_lsh() throws Exception { return this.parse_by_operator(COperator.left_shift); }

	@Override
	protected boolean bitws_rsh() throws Exception { return this.parse_by_operator(COperator.righ_shift); }

	@Override
	protected boolean logic_and() throws Exception { return this.parse_by_operator(COperator.logic_and); }

	@Override
	protected boolean logic_ior() throws Exception { return this.parse_by_operator(COperator.logic_or); }

	@Override
	protected boolean greater_tn() throws Exception {
		SymbolExpression condition = this.sym_expression(COperator.greater_eq, get_loperand(), get_roperand());
		return this.parse_by_condition_and_operator(condition, COperator.greater_tn);
	}

	@Override
	protected boolean greater_eq() throws Exception {
		SymbolExpression condition = this.sym_expression(COperator.greater_tn, get_loperand(), get_roperand());
		return this.parse_by_condition_and_muvalue(condition, Boolean.TRUE);
	}

	@Override
	protected boolean smaller_tn() throws Exception {
		SymbolExpression condition = this.sym_expression(COperator.smaller_eq, get_loperand(), get_roperand());
		return this.parse_by_condition_and_operator(condition, COperator.smaller_tn);
	}

	@Override
	protected boolean smaller_eq() throws Exception {
		SymbolExpression condition = this.sym_expression(COperator.smaller_tn, get_loperand(), get_roperand());
		return this.parse_by_condition_and_muvalue(condition, Boolean.TRUE);
	}

	@Override
	protected boolean equal_with() throws Exception { return this.report_equivalent_mutation(); }

	@Override
	protected boolean not_equals() throws Exception {
		return this.parse_by_condition_and_operator(Boolean.TRUE, COperator.not_equals);
	}

	@Override
	protected boolean to_assign() throws Exception { return this.report_unsupport_exception(); }

}
