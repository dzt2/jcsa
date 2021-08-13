package com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirOperatorParser;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class CirSetArithModParser extends CirOperatorParser {

	@Override
	protected boolean to_assign() throws Exception {
		/** mutation [x % y; y] :: TRUE **/
		CirAttribute constraint, init_error;
		constraint = this.get_constraint(Boolean.TRUE);
		init_error = this.mut_expression(this.roperand);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_add() throws Exception {
		/** mutation [x % y; x + y] :: (x + y != 0) **/
		CirAttribute constraint, init_error;
		SymbolExpression condition, muta_expression;
		
		condition = this.sym_expression(COperator.arith_add, loperand, roperand);
		constraint = this.new_constraint(SymbolFactory.not_equals(condition, 0));
		
		muta_expression = this.sym_expression(COperator.arith_add, loperand, roperand);
		init_error = this.mut_expression(muta_expression);
		
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_sub() throws Exception {
		/** mutation [x % y; x - y] :: (x < y || x > 2y) **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		List<SymbolExpression> conditions = new ArrayList<SymbolExpression>();
		
		conditions.add(this.sym_expression(COperator.smaller_tn, loperand, roperand));
		conditions.add(this.sym_expression(COperator.greater_tn, loperand, 
					this.sym_expression(COperator.arith_mul, roperand, 2)));
		constraint = this.disjuncts(conditions);
		
		muta_expression = this.sym_expression(COperator.arith_sub, loperand, roperand);
		init_error = this.mut_expression(muta_expression);
		
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_mul() throws Exception {
		/** mutation [x % y; x * y] :: (x != 0) **/
		CirAttribute constraint, init_error;
		constraint = this.get_constraint(SymbolFactory.not_equals(loperand, 0));
		init_error = this.mut_expression(this.sym_expression(COperator.arith_mul, loperand, roperand));
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_div() throws Exception {
		/** mutation [x % y; x / y] :: (x != 0) **/
		CirAttribute constraint, init_error;
		constraint = this.get_constraint(SymbolFactory.not_equals(loperand, 0));
		init_error = this.mut_expression(this.sym_expression(COperator.arith_div, loperand, roperand));
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_mod() throws Exception {
		return this.report_equivalence_mutation();
	}

	@Override
	protected boolean bitws_and() throws Exception {
		/** mutation [x % y; x * y] :: (x != 0) **/
		CirAttribute constraint, init_error;
		constraint = this.get_constraint(SymbolFactory.not_equals(loperand, 0));
		init_error = this.mut_expression(this.sym_expression(COperator.bit_and, loperand, roperand));
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		/** mutation [x % y; x | y] :: (TRUE) **/
		CirAttribute constraint, init_error;
		constraint = this.get_constraint(Boolean.TRUE);
		init_error = this.mut_expression(this.sym_expression(COperator.bit_or, loperand, roperand));
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		/** mutation [x % y; x ^ y] :: (x & y != y) **/
		CirAttribute constraint, init_error; 
		SymbolExpression condition, muta_expression;
		
		condition = this.sym_expression(COperator.bit_and, loperand, roperand);
		constraint = this.get_constraint(SymbolFactory.not_equals(condition, roperand));
		
		muta_expression = this.sym_expression(COperator.bit_xor, loperand, roperand);
		init_error = this.mut_expression(muta_expression);
		
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		/** mutation [x % y; x << y] :: (x != 0) **/
		CirAttribute constraint, init_error;
		constraint = this.get_constraint(SymbolFactory.not_equals(loperand, 0));
		init_error = this.mut_expression(this.sym_expression(COperator.left_shift, loperand, roperand));
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		/** mutation [x % y; x >> y] :: (x != 0) **/
		CirAttribute constraint, init_error;
		constraint = this.get_constraint(SymbolFactory.not_equals(loperand, 0));
		init_error = this.mut_expression(this.sym_expression(COperator.righ_shift, loperand, roperand));
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean logic_and() throws Exception {
		/** mutation [x % y; x && y] :: (x != 0) **/
		CirAttribute constraint, init_error;
		constraint = this.get_constraint(SymbolFactory.not_equals(loperand, 0));
		init_error = this.mut_expression(Boolean.TRUE);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean logic_ior() throws Exception {
		/** mutation [x % y; x || y] :: (TRUE) **/
		CirAttribute constraint, init_error;
		constraint = this.get_constraint(Boolean.TRUE);
		init_error = this.mut_expression(Boolean.TRUE);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean greater_tn() throws Exception {
		/** mutation [x % y; x - y > 0] :: (x - y is non_boolean) **/
		CirAttribute constraint, init_error;
		SymbolExpression condition, muta_expression;
		
		condition = this.sym_expression(COperator.arith_sub, loperand, roperand);
		constraint = this.get_constraint(this.non_bool_condition(condition));
		
		muta_expression = this.sym_expression(COperator.greater_tn, loperand, roperand);
		init_error = this.mut_expression(muta_expression);
		
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean greater_eq() throws Exception {
		/** mutation [x % y; x - y >= 0] :: (x - y != 1) **/
		CirAttribute constraint, init_error;
		SymbolExpression condition, muta_expression;
		
		condition = this.sym_expression(COperator.arith_sub, loperand, roperand);
		constraint = this.get_constraint(SymbolFactory.not_equals(condition, 1));
		
		muta_expression = this.sym_expression(COperator.greater_eq, loperand, roperand);
		init_error = this.mut_expression(muta_expression);
		
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean smaller_tn() throws Exception {
		/** mutation [x % y; x - y < 0] :: (x != y) **/
		CirAttribute constraint, init_error; 
		SymbolExpression muta_expression;
		
		constraint = this.get_constraint(SymbolFactory.not_equals(loperand, roperand));
		muta_expression = this.sym_expression(COperator.smaller_tn, loperand, roperand);
		init_error = this.mut_expression(muta_expression);
		
		return this.add_infection(constraint, init_error);
		
	}

	@Override
	protected boolean smaller_eq() throws Exception {
		/** mutation [x % y; x - y <= 0] :: (TRUE) **/
		CirAttribute constraint, init_error; 
		SymbolExpression muta_expression;
		
		constraint = this.get_constraint(Boolean.TRUE);
		muta_expression = this.sym_expression(COperator.smaller_eq, loperand, roperand);
		init_error = this.mut_expression(muta_expression);
		
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean equal_with() throws Exception {
		/** mutation [x % y; x == y] :: (TRUE) **/
		CirAttribute constraint, init_error; 
		SymbolExpression muta_expression;
		
		constraint = this.get_constraint(Boolean.TRUE);
		muta_expression = this.sym_expression(COperator.equal_with, loperand, roperand);
		init_error = this.mut_expression(muta_expression);
		
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean not_equals() throws Exception {
		/** mutation [x % y; x - y != 0] :: (x - y is non_boolean) **/
		CirAttribute constraint, init_error;
		SymbolExpression condition, muta_expression;
		
		condition = this.sym_expression(COperator.arith_sub, loperand, roperand);
		constraint = this.get_constraint(this.non_bool_condition(condition));
		
		muta_expression = this.sym_expression(COperator.not_equals, loperand, roperand);
		init_error = this.mut_expression(muta_expression);
		
		return this.add_infection(constraint, init_error);
	}

}
