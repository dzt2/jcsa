package com.jcsa.jcmutest.mutant.ctx2mutant.oprt;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.ctx2mutant.ContextMutations;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class CirLogicIorMutationParser extends CirOperatorMutationParser {

	@Override
	protected boolean arith_add() throws Exception { return this.logic_ior(); }

	@Override
	protected boolean arith_sub() throws Exception {
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.sym_condition(this.get_loperand(), true));
		conditions.add(this.sym_condition(this.get_roperand(), true));
		return this.parse_by_condition_and_muvalue(this.sym_conjunctions(conditions), Boolean.FALSE);
	}

	@Override
	protected boolean arith_mul() throws Exception { return this.logic_and(); }

	@Override
	protected boolean arith_div() throws Exception {
		this.parse_by_condition_and_muvalue(this.sym_condition(this.get_roperand(), false), ContextMutations.trap_value);
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.sym_condition(this.get_loperand(), false));
		conditions.add(this.sym_condition(this.get_roperand(), true));
		return this.parse_by_condition_and_muvalue(this.sym_conjunctions(conditions), Boolean.FALSE);
	}

	@Override
	protected boolean arith_mod() throws Exception {
		this.parse_by_condition_and_muvalue(this.sym_condition(this.get_roperand(), false), ContextMutations.trap_value);
		return this.parse_by_condition_and_muvalue(this.sym_condition(this.get_roperand(), true), Boolean.FALSE);
	}

	@Override
	protected boolean bitws_and() throws Exception { return this.logic_and(); }

	@Override
	protected boolean bitws_ior() throws Exception { return this.logic_ior(); }

	@Override
	protected boolean bitws_xor() throws Exception { return this.arith_sub(); }

	@Override
	protected boolean bitws_lsh() throws Exception {
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.sym_condition(this.get_loperand(), false));
		conditions.add(this.sym_condition(this.get_roperand(), true));
		return this.parse_by_condition_and_muvalue(this.sym_conjunctions(conditions), Boolean.FALSE);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		SymbolExpression condition = this.sym_condition(this.get_roperand(), true);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

	@Override
	protected boolean logic_and() throws Exception {
		SymbolExpression lcondition = this.sym_condition(this.get_loperand(), true);
		SymbolExpression rcondition = this.sym_condition(this.get_roperand(), true);
		SymbolExpression condition = this.neq_expression(lcondition, rcondition);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

	@Override
	protected boolean logic_ior() throws Exception { return this.report_equivalent_mutation(); }

	@Override
	protected boolean greater_tn() throws Exception {
		SymbolExpression condition = this.sym_condition(this.get_roperand(), true);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

	@Override
	protected boolean greater_eq() throws Exception {
		SymbolExpression condition = this.sym_condition(this.get_loperand(), false);
		SymbolExpression muvalue = this.sym_condition(this.get_expression(), false);
		return this.parse_by_condition_and_muvalue(condition, muvalue);
	}

	@Override
	protected boolean smaller_tn() throws Exception {
		SymbolExpression condition = this.sym_condition(this.get_loperand(), true);
		return this.parse_by_condition_and_muvalue(condition, Boolean.FALSE);
	}

	@Override
	protected boolean smaller_eq() throws Exception {
		SymbolExpression condition = this.sym_condition(this.get_roperand(), false);
		SymbolExpression muvalue = this.sym_condition(this.get_expression(), false);
		return this.parse_by_condition_and_muvalue(condition, muvalue);
	}

	@Override
	protected boolean equal_with() throws Exception { return this.parse_by_operator(COperator.equal_with); }

	@Override
	protected boolean not_equals() throws Exception { return this.arith_sub(); }

	@Override
	protected boolean to_assign() throws Exception { return this.report_unsupport_exception(); }

}
