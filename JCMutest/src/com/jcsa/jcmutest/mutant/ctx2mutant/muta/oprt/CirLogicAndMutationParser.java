package com.jcsa.jcmutest.mutant.ctx2mutant.muta.oprt;

import com.jcsa.jcmutest.mutant.ctx2mutant.ContextMutations;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class CirLogicAndMutationParser extends CirOperatorMutationParser {

	@Override
	protected boolean to_assign() throws Exception {
		return this.report_unsupport_exception();
	}

	@Override
	protected boolean arith_add() throws Exception {
		return this.logic_ior();
	}

	@Override
	protected boolean arith_sub() throws Exception {
		return this.not_equals();
	}

	@Override
	protected boolean arith_mul() throws Exception {
		return this.parse_by_equivalence();
	}

	@Override
	protected boolean arith_div() throws Exception {
		this.parse_by_condition_and_muvalue(this.sym_condition(this.get_loperand(), false), Boolean.FALSE);
		this.parse_by_condition_and_muvalue(this.sym_condition(this.get_roperand(), false), ContextMutations.trap_value);
		return true;
	}

	@Override
	protected boolean arith_mod() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean bitws_and() throws Exception {
		return this.parse_by_equivalence();
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		return this.logic_ior();
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		return this.not_equals();
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean logic_and() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean logic_ior() throws Exception {
		SymbolExpression lcondition = this.sym_condition(this.get_loperand());
		SymbolExpression rcondition = this.sym_condition(this.get_roperand());
		SymbolExpression condition = this.sym_expression(COperator.not_equals, lcondition, rcondition);
		return this.parse_by_condition_and_operator(condition, COperator.logic_or);
	}

	@Override
	protected boolean greater_tn() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean greater_eq() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean smaller_tn() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean smaller_eq() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean equal_with() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean not_equals() throws Exception {
		SymbolExpression lcondition = this.sym_condition(this.get_loperand());
		SymbolExpression rcondition = this.sym_condition(this.get_roperand());
		this.parse_by_condition_and_muvalue(this.sym_expression(COperator.not_equals, lcondition, rcondition), Boolean.TRUE);
		this.parse_by_condition_and_muvalue(this.sym_expression(COperator.logic_and, lcondition, rcondition), Boolean.FALSE);
		return true;
	}

}
