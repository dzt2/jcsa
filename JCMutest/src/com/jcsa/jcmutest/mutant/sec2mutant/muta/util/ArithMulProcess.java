package com.jcsa.jcmutest.mutant.sec2mutant.muta.util;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.sec2mutant.lang.SecFactory;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecConstraint;
import com.jcsa.jcmutest.mutant.sec2mutant.lang.desc.SecDescription;
import com.jcsa.jcmutest.mutant.sec2mutant.muta.SetOperatorProcess;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.sym.SymExpression;
import com.jcsa.jcparse.lang.sym.SymFactory;
import com.jcsa.jcparse.lang.sym.SymIdentifier;

public class ArithMulProcess extends SetOperatorProcess {

	@Override
	protected void to_assign() throws Exception {
		this.unsupport_operation();
	}

	@Override
	protected void arith_add() throws Exception {
		/**
		 * 	[x != 0 || y != 0] --> set_expr(x + y)
		 */
		SecDescription constraint; SecDescription init_error;
		List<SecDescription> constraints = new ArrayList<SecDescription>();
		
		constraints.add(this.get_constraint(SymFactory.
				not_equals(this.loperand, Integer.valueOf(0))));
		constraints.add(this.get_constraint(SymFactory.
				not_equals(this.roperand, Integer.valueOf(0))));
		constraint = SecFactory.disjunct(statement, constraints);
		
		if(this.compare) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(SymFactory.arith_add(
					expression.get_data_type(), loperand, roperand));
		}
		this.add_infection(constraint, init_error);
	}

	@Override
	protected void arith_sub() throws Exception {
		/**
		 * 	[x != 0 || y != 0] --> set_expr(x - y)
		 */
		SecDescription constraint; SecDescription init_error;
		List<SecDescription> constraints = new ArrayList<SecDescription>();
		
		constraints.add(this.get_constraint(SymFactory.
				not_equals(this.loperand, Integer.valueOf(0))));
		constraints.add(this.get_constraint(SymFactory.
				not_equals(this.roperand, Integer.valueOf(0))));
		constraint = SecFactory.disjunct(statement, constraints);
		
		if(this.compare) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(SymFactory.arith_sub(
					expression.get_data_type(), loperand, roperand));
		}
		this.add_infection(constraint, init_error);
	}

	@Override
	protected void arith_mul() throws Exception {
		this.unsupport_operation();
	}

	@Override
	protected void arith_div() throws Exception {
		/**
		 * 	[y == 0] --> trap_stmt()
		 * 	[x != 0] --> set_expr(x / y)
		 */
		SecDescription constraint; SecDescription init_error;
		List<SecDescription> constraints = new ArrayList<SecDescription>();
		
		if(this.compare) {
			constraints.add(this.get_constraint(SymFactory.
					equal_with(roperand, Integer.valueOf(0))));
			constraints.add(this.get_constraint(SymFactory.
					not_equals(loperand, Integer.valueOf(0))));
			constraint = SecFactory.disjunct(statement, constraints);
			init_error = this.trap_statement();
			this.add_infection(constraint, init_error);
		}
		else {
			constraint = this.get_constraint(SymFactory.
					equal_with(this.roperand, Integer.valueOf(0)));
			init_error = this.trap_statement();
			this.add_infection(constraint, init_error);
			
			constraint = this.get_constraint(SymFactory.
					not_equals(this.loperand, Integer.valueOf(0)));
			init_error = this.set_expression(SymFactory.arith_div(
					expression.get_data_type(), loperand, roperand));
			this.add_infection(constraint, init_error);
		}
	}

	@Override
	protected void arith_mod() throws Exception {
		/**
		 * 	[y == 0] --> trap_stmt()
		 * 	[x != 0] --> set_expr(x % y)
		 */
		SecDescription constraint; SecDescription init_error;
		List<SecDescription> constraints = new ArrayList<SecDescription>();
		
		if(this.compare) {
			constraints.add(this.get_constraint(SymFactory.
					equal_with(roperand, Integer.valueOf(0))));
			constraints.add(this.get_constraint(SymFactory.
					not_equals(loperand, Integer.valueOf(0))));
			constraint = SecFactory.disjunct(statement, constraints);
			init_error = this.trap_statement();
			this.add_infection(constraint, init_error);
		}
		else {
			constraint = this.get_constraint(SymFactory.
					equal_with(this.roperand, Integer.valueOf(0)));
			init_error = this.trap_statement();
			this.add_infection(constraint, init_error);
			
			constraint = this.get_constraint(SymFactory.
					not_equals(this.loperand, Integer.valueOf(0)));
			init_error = this.set_expression(SymFactory.arith_mod(
					expression.get_data_type(), loperand, roperand));
			this.add_infection(constraint, init_error);
		}
	}
	
	@Override
	protected void bitws_and() throws Exception {
		/**
		 * 	[x != 0 && y != 0] --> set_expr(x & y)
		 */
		SecDescription constraint; SecDescription init_error;
		List<SecDescription> constraints = new ArrayList<SecDescription>();
		
		constraints.add(this.get_constraint(SymFactory.
				not_equals(loperand, Integer.valueOf(0))));
		constraints.add(this.get_constraint(SymFactory.
				not_equals(roperand, Integer.valueOf(0))));
		constraint = SecFactory.conjunct(statement, constraints);
		
		if(this.compare) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(SymFactory.bitws_and(
					expression.get_data_type(), loperand, roperand));
		}
		this.add_infection(constraint, init_error);
	}

	@Override
	protected void bitws_ior() throws Exception {
		/**
		 * 	[x != 0 || y != 0] --> set_expr(x | y)
		 */
		SecDescription constraint; SecDescription init_error;
		List<SecDescription> constraints = new ArrayList<SecDescription>();
		
		constraints.add(this.get_constraint(SymFactory.
				not_equals(loperand, Integer.valueOf(0))));
		constraints.add(this.get_constraint(SymFactory.
				not_equals(roperand, Integer.valueOf(0))));
		constraint = SecFactory.disjunct(statement, constraints);
		
		if(this.compare) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(SymFactory.bitws_ior(
					expression.get_data_type(), loperand, roperand));
		}
		this.add_infection(constraint, init_error);
	}

	@Override
	protected void bitws_xor() throws Exception {
		/**
		 * 	[x != 0 || y != 0] --> set_expr(x | y)
		 */
		SecDescription constraint; SecDescription init_error;
		List<SecDescription> constraints = new ArrayList<SecDescription>();
		
		constraints.add(this.get_constraint(SymFactory.
				not_equals(loperand, Integer.valueOf(0))));
		constraints.add(this.get_constraint(SymFactory.
				not_equals(roperand, Integer.valueOf(0))));
		constraint = SecFactory.disjunct(statement, constraints);
		
		if(this.compare) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(SymFactory.bitws_xor(
					expression.get_data_type(), loperand, roperand));
		}
		this.add_infection(constraint, init_error);
	}

	@Override
	protected void bitws_lsh() throws Exception {
		/**
		 * 	[x != 0] --> set_expr(x << y)
		 */
		SecConstraint constraint; SecDescription init_error;
		constraint = this.get_constraint(SymFactory.
				not_equals(this.loperand, Integer.valueOf(0)));
		
		if(this.compare) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(SymFactory.bitws_lsh(
					expression.get_data_type(), loperand, roperand));
		}
		this.add_infection(constraint, init_error);
	}

	@Override
	protected void bitws_rsh() throws Exception {
		/**
		 * 	[x != 0] --> set_expr(x >> y)
		 */
		SecConstraint constraint; SecDescription init_error;
		constraint = this.get_constraint(SymFactory.
				not_equals(this.loperand, Integer.valueOf(0)));
		
		if(this.compare) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(SymFactory.bitws_rsh(
					expression.get_data_type(), loperand, roperand));
		}
		this.add_infection(constraint, init_error);
	}

	@Override
	protected void logic_and() throws Exception {
		/**
		 * 	[x != AnyBool && y != AnyBool]
		 */
		SecDescription constraint; SecDescription init_error;
		List<SecDescription> constraints = new ArrayList<SecDescription>();
		
		SymExpression any_bool = SymFactory.new_identifier(
				CBasicTypeImpl.bool_type, SymIdentifier.AnyBoolean);
		constraints.add(this.get_constraint(SymFactory.not_equals(loperand, any_bool)));
		constraints.add(this.get_constraint(SymFactory.not_equals(roperand, any_bool)));
		constraint = SecFactory.conjunct(statement, constraints);
		
		if(this.compare) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(SymFactory.logic_and(loperand, roperand));
		}
		this.add_infection(constraint, init_error);
	}

	@Override
	protected void logic_ior() throws Exception {
		/**
		 * 	[x != 0 || y != 0] --> set_expr(x | y)
		 */
		SecDescription constraint; SecDescription init_error;
		List<SecDescription> constraints = new ArrayList<SecDescription>();
		
		constraints.add(this.get_constraint(SymFactory.
				not_equals(loperand, Integer.valueOf(0))));
		constraints.add(this.get_constraint(SymFactory.
				not_equals(roperand, Integer.valueOf(0))));
		constraint = SecFactory.disjunct(statement, constraints);
		
		if(this.compare) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(
					SymFactory.logic_ior(loperand, roperand));
		}
		this.add_infection(constraint, init_error);
	}
	
	@Override
	protected void greater_tn() throws Exception {
		/**
		 * 	[true] --> set_expr(xxx)
		 */
		SecConstraint constraint = this.get_constraint(Boolean.TRUE);
		SecDescription init_error;
		if(this.compare) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(SymFactory.greater_tn(loperand, roperand));
		}
		this.add_infection(constraint, init_error);
	}

	@Override
	protected void greater_eq() throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE);
		SecDescription init_error;
		if(this.compare) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(SymFactory.greater_eq(loperand, roperand));
		}
		this.add_infection(constraint, init_error);
	}

	@Override
	protected void smaller_tn() throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE);
		SecDescription init_error;
		if(this.compare) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(SymFactory.smaller_tn(loperand, roperand));
		}
		this.add_infection(constraint, init_error);
	}

	@Override
	protected void smaller_eq() throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE);
		SecDescription init_error;
		if(this.compare) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(SymFactory.smaller_eq(loperand, roperand));
		}
		this.add_infection(constraint, init_error);
	}

	@Override
	protected void equal_with() throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE);
		SecDescription init_error;
		if(this.compare) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(SymFactory.equal_with(loperand, roperand));
		}
		this.add_infection(constraint, init_error);
	}

	@Override
	protected void not_equals() throws Exception {
		SecConstraint constraint = this.get_constraint(Boolean.TRUE);
		SecDescription init_error;
		if(this.compare) {
			init_error = this.trap_statement();
		}
		else {
			init_error = this.set_expression(SymFactory.not_equals(loperand, roperand));
		}
		this.add_infection(constraint, init_error);
	}

}
