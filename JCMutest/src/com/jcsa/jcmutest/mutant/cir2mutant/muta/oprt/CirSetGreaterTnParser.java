package com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt;

import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirOperatorParser;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class CirSetGreaterTnParser extends CirOperatorParser {

	@Override
	protected boolean to_assign() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean arith_add() throws Exception {
		/** mutation [x - y > 0; x + y != 0] :: (TRUE) **/
		CirAttribute constraint, init_error; 
		SymbolExpression muta_expression;
		constraint = this.get_constraint(Boolean.TRUE);
		
		muta_expression = this.sym_expression(COperator.arith_add, loperand, roperand);
		init_error = this.mut_expression(SymbolFactory.not_equals(muta_expression, 0));
		
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_sub() throws Exception {
		/** mutation [x - y > 0; x - y != 0] :: (x < y; true) **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		constraint = this.get_constraint(SymbolFactory.smaller_tn(loperand, roperand));
		muta_expression = SymbolFactory.sym_expression(Boolean.TRUE);
		init_error = this.mut_expression(muta_expression);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_mul() throws Exception {
		/** mutation [x - y > 0; x * y != 0] :: TRUE **/
		CirAttribute constraint, init_error; 
		SymbolExpression muta_expression;
		constraint = this.get_constraint(Boolean.TRUE);
		
		muta_expression = this.sym_expression(COperator.arith_mul, loperand, roperand);
		init_error = this.mut_expression(SymbolFactory.not_equals(muta_expression, 0));
		
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_div() throws Exception {
		/** mutation [x - y > 0; x / y != 0] :: TRUE **/
		CirAttribute constraint, init_error; 
		SymbolExpression muta_expression;
		constraint = this.get_constraint(Boolean.TRUE);
		
		muta_expression = this.sym_expression(COperator.arith_div, loperand, roperand);
		init_error = this.mut_expression(SymbolFactory.not_equals(muta_expression, 0));
		
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_mod() throws Exception {
		/** mutation [x - y > 0; x % y != 0] :: (x < y; muta_expr) **/
		CirAttribute constraint, init_error; 
		SymbolExpression muta_expression;
		constraint = this.get_constraint(SymbolFactory.smaller_tn(loperand, roperand));
		
		muta_expression = this.sym_expression(COperator.arith_mod, loperand, roperand);
		init_error = this.mut_expression(SymbolFactory.not_equals(muta_expression, 0));
		
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_and() throws Exception {
		/** mutation [x - y > 0; x & y != 0] :: TRUE **/
		CirAttribute constraint, init_error; 
		SymbolExpression muta_expression;
		constraint = this.get_constraint(Boolean.TRUE);
		
		muta_expression = this.sym_expression(COperator.bit_and, loperand, roperand);
		init_error = this.mut_expression(SymbolFactory.not_equals(muta_expression, 0));
		
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		/** mutation [x - y > 0; x | y != 0] :: TRUE **/
		CirAttribute constraint, init_error; 
		SymbolExpression muta_expression;
		constraint = this.get_constraint(Boolean.TRUE);
		
		muta_expression = this.sym_expression(COperator.bit_or, loperand, roperand);
		init_error = this.mut_expression(SymbolFactory.not_equals(muta_expression, 0));
		
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		/** mutation [x - y > 0; x ^ y != 0] :: (x < y; true) **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		constraint = this.get_constraint(SymbolFactory.smaller_tn(loperand, roperand));
		muta_expression = SymbolFactory.sym_expression(Boolean.TRUE);
		init_error = this.mut_expression(muta_expression);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		/** mutation [x - y > 0; x << y != 0] :: TRUE **/
		CirAttribute constraint, init_error; 
		SymbolExpression muta_expression;
		constraint = this.get_constraint(Boolean.TRUE);
		
		muta_expression = this.sym_expression(COperator.left_shift, loperand, roperand);
		init_error = this.mut_expression(SymbolFactory.not_equals(muta_expression, 0));
		
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		/** mutation [x - y > 0; x >> y != 0] :: TRUE **/
		CirAttribute constraint, init_error; 
		SymbolExpression muta_expression;
		constraint = this.get_constraint(Boolean.TRUE);
		
		muta_expression = this.sym_expression(COperator.righ_shift, loperand, roperand);
		init_error = this.mut_expression(SymbolFactory.not_equals(muta_expression, 0));
		
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean logic_and() throws Exception {
		/** mutation [x > y; x && y] :: TRUE **/
		CirAttribute constraint, init_error; 
		SymbolExpression muta_expression;
		constraint = this.get_constraint(Boolean.TRUE);
		
		muta_expression = this.sym_expression(COperator.logic_and, loperand, roperand);
		init_error = this.mut_expression(muta_expression);
		
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean logic_ior() throws Exception {
		CirAttribute constraint, init_error; 
		SymbolExpression muta_expression;
		constraint = this.get_constraint(Boolean.TRUE);
		
		muta_expression = this.sym_expression(COperator.logic_or, loperand, roperand);
		init_error = this.mut_expression(muta_expression);
		
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean greater_tn() throws Exception {
		return this.report_equivalence_mutation();
	}

	@Override
	protected boolean greater_eq() throws Exception {
		/** mutation [x > y; x >= y] :: (x == y) **/
		CirAttribute constraint, init_error;
		constraint = this.get_constraint(SymbolFactory.equal_with(loperand, roperand));
		init_error = this.mut_expression(Boolean.TRUE);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean smaller_tn() throws Exception {
		/** mutation [x > y; x < y] :: (x != y) **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		constraint = this.get_constraint(SymbolFactory.not_equals(loperand, roperand));
		muta_expression = SymbolFactory.sym_condition(this.expression, false);
		init_error = this.mut_expression(muta_expression);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean smaller_eq() throws Exception {
		/** mutation [x > y; x <= y] :: TRUE **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		constraint = this.get_constraint(Boolean.TRUE);
		muta_expression = SymbolFactory.sym_condition(this.expression, false);
		init_error = this.mut_expression(muta_expression);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean equal_with() throws Exception {
		/** mutation [x > y; x == y] :: (x >= y) **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		constraint = this.get_constraint(SymbolFactory.greater_eq(loperand, roperand));
		muta_expression = SymbolFactory.sym_condition(this.expression, false);
		init_error = this.mut_expression(muta_expression);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean not_equals() throws Exception {
		/** mutation [x > y; x != y] :: (x < y) **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		constraint = this.get_constraint(SymbolFactory.smaller_tn(loperand, roperand));
		muta_expression = SymbolFactory.sym_expression(Boolean.TRUE);
		init_error = this.mut_expression(muta_expression);
		return this.add_infection(constraint, init_error);
	}

}
