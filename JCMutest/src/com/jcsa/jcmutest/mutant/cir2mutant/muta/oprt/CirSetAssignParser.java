package com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirOperatorParser;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class CirSetAssignParser extends CirOperatorParser {

	@Override
	protected boolean to_assign() throws Exception {
		return this.report_equivalence_mutation();
	}

	@Override
	protected boolean arith_add() throws Exception {
		/** [x = y; x = x + y] :: (x != 0) **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		constraint = this.get_constraint(SymbolFactory.not_equals(loperand, 0));
		muta_expression = this.sym_expression(COperator.arith_add, loperand, roperand);
		init_error = this.mut_expression(muta_expression);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_sub() throws Exception {
		/** [x = y; x = x - y] :: (x != 2y) **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		constraint = this.get_constraint(SymbolFactory.not_equals(loperand, 
				this.sym_expression(COperator.arith_mul, roperand, 2)));
		muta_expression = this.sym_expression(COperator.arith_sub, loperand, roperand);
		init_error = this.mut_expression(muta_expression);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_mul() throws Exception {
		/** [x = y; x = x * y] :: (x != 1; y != 0) **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		List<SymbolExpression> conditions = new ArrayList<SymbolExpression>();
		
		conditions.add(SymbolFactory.not_equals(loperand, 1));
		conditions.add(SymbolFactory.not_equals(roperand, 0));
		constraint = this.conjuncts(conditions);
		
		muta_expression = this.sym_expression(COperator.arith_mul, loperand, roperand);
		init_error = this.mut_expression(muta_expression);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_div() throws Exception {
		/** [x = y; x = x / y] :: (x != y * y) **/
		CirAttribute constraint, init_error; 
		SymbolExpression condition, muta_expression;
		
		condition = this.sym_expression(COperator.arith_mul, roperand, roperand);
		constraint = this.get_constraint(SymbolFactory.not_equals(loperand, condition));
		
		muta_expression = this.sym_expression(COperator.arith_div, loperand, roperand);
		init_error = this.mut_expression(muta_expression);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_mod() throws Exception {
		/** [x = y; x = x % y] :: (TRUE) **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		constraint = this.get_constraint(Boolean.TRUE);
		muta_expression = this.sym_expression(COperator.arith_mod, loperand, roperand);
		init_error = this.mut_expression(muta_expression);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_and() throws Exception {
		/** [x = y; x = x & y] :: (x != ~1; y != 0) **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		List<SymbolExpression> conditions = new ArrayList<SymbolExpression>();
		
		conditions.add(SymbolFactory.not_equals(loperand, ~1));
		conditions.add(SymbolFactory.not_equals(roperand, 0));
		constraint = this.conjuncts(conditions);
		
		muta_expression = this.sym_expression(COperator.bit_and, loperand, roperand);
		init_error = this.mut_expression(muta_expression);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		/** [x = y; x = x | y] :: (x != 0) **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		constraint = this.get_constraint(SymbolFactory.not_equals(loperand, 0));
		muta_expression = this.sym_expression(COperator.bit_or, loperand, roperand);
		init_error = this.mut_expression(muta_expression);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		/** [x = y; x = x | y] :: (x != 0) **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		constraint = this.get_constraint(SymbolFactory.not_equals(loperand, 0));
		muta_expression = this.sym_expression(COperator.bit_xor, loperand, roperand);
		init_error = this.mut_expression(muta_expression);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		constraint = this.get_constraint(Boolean.TRUE);
		muta_expression = this.sym_expression(COperator.left_shift, loperand, roperand);
		init_error = this.mut_expression(muta_expression);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		constraint = this.get_constraint(Boolean.TRUE);
		muta_expression = this.sym_expression(COperator.righ_shift, loperand, roperand);
		init_error = this.mut_expression(muta_expression);
		return this.add_infection(constraint, init_error);
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
