package com.jcsa.jcmutest.mutant.cir2mutant.muta.oprt;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.cir2mutant.base.CirAttribute;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirOperatorParser;
import com.jcsa.jcparse.lang.symbol.SymbolExpression;
import com.jcsa.jcparse.lang.symbol.SymbolFactory;

public class CirSetLogicAndParser extends CirOperatorParser {

	@Override
	protected boolean to_assign() throws Exception {
		return this.unsupport_exception();
	}

	@Override
	protected boolean arith_add() throws Exception {
		/** mutation [x && y; x + y] :: (tf||ft) **/
		CirAttribute constraint, init_error; 
		SymbolExpression lcondition, rcondition;
		
		lcondition = SymbolFactory.sym_condition(loperand, true);
		rcondition = SymbolFactory.sym_condition(roperand, true);
		constraint = this.get_constraint(SymbolFactory.not_equals(lcondition, rcondition));
		
		init_error = this.mut_expression(Boolean.TRUE);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_sub() throws Exception {
		/** mutation [x && y; x - y] :: (f or f) **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		List<SymbolExpression> conditions = new ArrayList<SymbolExpression>();
		
		conditions.add(SymbolFactory.sym_condition(loperand, false));
		conditions.add(SymbolFactory.sym_condition(roperand, false));
		constraint = this.disjuncts(conditions);
		
		muta_expression = SymbolFactory.sym_condition(this.expression, false);
		init_error = this.mut_expression(muta_expression);
		
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_mul() throws Exception {
		/** equivalent mutant **/
		CirAttribute constraint, init_error;
		constraint = this.get_constraint(Boolean.FALSE);
		init_error = this.mut_expression(this.expression);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_div() throws Exception {
		/** mutation [x && y; x / y] :: (y == 0) **/
		CirAttribute constraint, init_error;
		constraint = this.get_constraint(SymbolFactory.sym_condition(roperand, false));
		init_error = this.trap_statement();
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean arith_mod() throws Exception {
		/** mutation [x && y; x % y] :: (y == 0) || (tt) **/
		CirAttribute constraint, init_error;
		List<SymbolExpression> conditions = new ArrayList<SymbolExpression>();
		
		/** (y == 0) --> trap_stmt **/
		constraint = this.get_constraint(SymbolFactory.sym_condition(roperand, false));
		init_error = this.trap_statement();
		this.add_infection(constraint, init_error);
		
		/** (tt) --> false **/
		conditions.add(SymbolFactory.sym_condition(loperand, true));
		conditions.add(SymbolFactory.sym_condition(roperand, true));
		constraint = this.conjuncts(conditions);
		init_error = this.mut_expression(Boolean.FALSE);
		this.add_infection(constraint, init_error);
		
		return true;
	}

	@Override
	protected boolean bitws_and() throws Exception {
		/** equivalent mutant **/
		CirAttribute constraint, init_error;
		constraint = this.get_constraint(Boolean.FALSE);
		init_error = this.mut_expression(this.expression);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_ior() throws Exception {
		CirAttribute constraint, init_error; 
		SymbolExpression lcondition, rcondition;
		
		lcondition = SymbolFactory.sym_condition(loperand, true);
		rcondition = SymbolFactory.sym_condition(roperand, true);
		constraint = this.get_constraint(SymbolFactory.not_equals(lcondition, rcondition));
		
		init_error = this.mut_expression(Boolean.TRUE);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_xor() throws Exception {
		/** mutation [x && y; x ^ y] :: (f or f) **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		List<SymbolExpression> conditions = new ArrayList<SymbolExpression>();
		
		conditions.add(SymbolFactory.sym_condition(loperand, false));
		conditions.add(SymbolFactory.sym_condition(roperand, false));
		constraint = this.disjuncts(conditions);
		
		muta_expression = SymbolFactory.sym_condition(this.expression, false);
		init_error = this.mut_expression(muta_expression);
		
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_lsh() throws Exception {
		/** mutation [x && y; x << y] :: (tf) **/
		CirAttribute constraint, init_error; 
		List<SymbolExpression> conditions = new ArrayList<SymbolExpression>();
		
		conditions.add(SymbolFactory.sym_condition(loperand, true));
		conditions.add(SymbolFactory.sym_condition(roperand, false));
		constraint = this.conjuncts(conditions);
		
		init_error = this.mut_expression(Boolean.TRUE);
		
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean bitws_rsh() throws Exception {
		/** mutation [x && y; x >> y] :: (tf) || (tt) **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		constraint = this.get_constraint(SymbolFactory.sym_condition(loperand, true));
		muta_expression = SymbolFactory.sym_condition(this.expression, false);
		init_error = this.mut_expression(muta_expression);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean logic_and() throws Exception {
		return this.report_equivalence_mutation();
	}

	@Override
	protected boolean logic_ior() throws Exception {
		/** mutation [x && y; x || y] :: (tf||ft) **/
		CirAttribute constraint, init_error; 
		SymbolExpression lcondition, rcondition;
		
		lcondition = SymbolFactory.sym_condition(loperand, true);
		rcondition = SymbolFactory.sym_condition(roperand, true);
		constraint = this.get_constraint(SymbolFactory.not_equals(lcondition, rcondition));
		
		init_error = this.mut_expression(Boolean.TRUE);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean greater_tn() throws Exception {
		/** mutation [x && y; x > y] :: (tf, tt) **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		constraint = this.get_constraint(SymbolFactory.sym_condition(loperand, true));
		muta_expression = SymbolFactory.sym_condition(this.expression, false);
		init_error = this.mut_expression(muta_expression);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean greater_eq() throws Exception {
		/** mutation [x && y; x >= y] :: (ff, tf) **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		constraint = this.get_constraint(SymbolFactory.sym_condition(roperand, false));
		muta_expression = SymbolFactory.sym_expression(Boolean.TRUE);
		init_error = this.mut_expression(muta_expression);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean smaller_tn() throws Exception {
		/** mutation [x && y; x < y] :: (ft, tt) **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		constraint = this.get_constraint(SymbolFactory.sym_condition(roperand, true));
		muta_expression = SymbolFactory.sym_condition(this.expression, false);
		init_error = this.mut_expression(muta_expression);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean smaller_eq() throws Exception {
		/** mutation [x && y; x <= y] :: (ff, ft) **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		constraint = this.get_constraint(SymbolFactory.sym_condition(loperand, false));
		muta_expression = SymbolFactory.sym_expression(Boolean.TRUE);
		init_error = this.mut_expression(muta_expression);
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean equal_with() throws Exception {
		/** mutation [x && y; x == y] :: (ff) **/
		CirAttribute constraint, init_error;
		List<SymbolExpression> conditions = new ArrayList<SymbolExpression>();
		
		conditions.add(SymbolFactory.sym_condition(loperand, false));
		conditions.add(SymbolFactory.sym_condition(roperand, false));
		constraint = this.conjuncts(conditions);
		init_error = this.mut_expression(Boolean.TRUE);
		
		return this.add_infection(constraint, init_error);
	}

	@Override
	protected boolean not_equals() throws Exception {
		/** mutation [x && y; x != y] :: (tt, tf, ft) **/
		CirAttribute constraint, init_error; SymbolExpression muta_expression;
		List<SymbolExpression> conditions = new ArrayList<SymbolExpression>();
		
		conditions.add(SymbolFactory.sym_condition(loperand, true));
		conditions.add(SymbolFactory.sym_condition(roperand, true));
		constraint = this.disjuncts(conditions);
		
		muta_expression = SymbolFactory.sym_condition(this.expression, false);
		init_error = this.mut_expression(muta_expression);
		
		return this.add_infection(constraint, init_error);
	}

}
