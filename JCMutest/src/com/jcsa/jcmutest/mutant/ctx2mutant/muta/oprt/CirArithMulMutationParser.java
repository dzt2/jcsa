package com.jcsa.jcmutest.mutant.ctx2mutant.muta.oprt;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;

public class CirArithMulMutationParser extends CirOperatorMutationParser {

	@Override
	protected boolean to_assign() throws Exception {
		/** (x *= y; x = y) :: (x * y != y) --> (x = y) **/
		return this.parse_by_operator(COperator.assign);
	}

	@Override
	protected boolean arith_add() throws Exception {
		/** (x * y; x + y) **/
		return this.parse_by_operator(COperator.arith_add);
	}

	@Override
	protected boolean arith_sub() throws Exception {
		/** (x * y; x - y) **/
		return this.parse_by_operator(COperator.arith_sub);
	}
	
	@Override
	protected boolean arith_mul() throws Exception {
		return this.report_equivalences();
	}
	
	@Override
	protected boolean arith_div() throws Exception {
		/** (x * y, x / y) :: (x != 0 and y != 1 and y != -1) --> (x / y) **/
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.sym_expression(COperator.not_equals, this.get_loperand(), 0));
		conditions.add(this.sym_expression(COperator.not_equals, this.get_roperand(), 1));
		conditions.add(this.sym_expression(COperator.not_equals, this.get_roperand(), -1));
		return this.parse_by_condition_and_operator(this.sym_conjunction(conditions), COperator.arith_div);
	}
	
	@Override
	protected boolean arith_mod() throws Exception {
		/** (x * y, x % y) :: (x != 0) --> (x % y) **/
		SymbolExpression condition = this.sym_expression(COperator.not_equals, this.get_loperand(), 0);
		return this.parse_by_condition_and_operator(condition, COperator.arith_mod);
	}

	@Override
	protected boolean bitws_and() throws Exception {
		/** (x * y, x / y) :: (x != 0 and y != 1 and y != -1) --> (x / y) **/
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.sym_expression(COperator.not_equals, this.get_loperand(), 0));
		conditions.add(this.sym_expression(COperator.not_equals, this.get_roperand(), 0));
		return this.parse_by_condition_and_operator(this.sym_conjunction(conditions), COperator.bit_and);
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		return this.parse_by_operator(COperator.bit_or);
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		return this.parse_by_operator(COperator.bit_xor);
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		SymbolExpression condition = this.sym_expression(COperator.not_equals, this.get_loperand(), 0);
		return this.parse_by_condition_and_operator(condition, COperator.left_shift);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		SymbolExpression condition = this.sym_expression(COperator.not_equals, this.get_loperand(), 0);
		return this.parse_by_condition_and_operator(condition, COperator.righ_shift);
	}

	@Override
	protected boolean logic_and() throws Exception {
		/** (x * y, x / y) :: (x != 0 and y != 1 and y != -1) --> (x / y) **/
		List<Object> conditions = new ArrayList<Object>();
		conditions.add(this.sym_expression(COperator.not_equals, this.get_loperand(), 0));
		conditions.add(this.sym_expression(COperator.not_equals, this.get_roperand(), 0));
		return this.parse_by_condition_and_operator(this.sym_conjunction(conditions), COperator.logic_and);
	}

	@Override
	protected boolean logic_ior() throws Exception {
		return this.parse_by_operator(COperator.logic_or);
	}

	@Override
	protected boolean greater_tn() throws Exception {
		return this.parse_by_operator(COperator.greater_tn);
	}

	@Override
	protected boolean greater_eq() throws Exception {
		return this.parse_by_operator(COperator.greater_eq);
	}

	@Override
	protected boolean smaller_tn() throws Exception {
		return this.parse_by_operator(COperator.smaller_tn);
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
		return this.parse_by_operator(COperator.not_equals);
	}

}
