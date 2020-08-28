package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect.oxxn;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class ArithAddSadInfection extends OXXNSadInfection {

	/**
	 * equivalent mutation case
	 */
	protected void set_arith_add(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception { /* equivalent mutation */ }
	/**
	 * 	[roperand != 0]
	 * 	-->
	 * 	assert#stmt:set_expr(expression, x - y)	&&
	 * 	assert#stmt:add_operand(expression, +, -2 * y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	protected void set_arith_sub(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		List<SadAssertion> state_errors = new ArrayList<SadAssertion>();
		
		condition = this.not_equals(roperand, 0);
		constraint = SadFactory.assert_condition(statement, condition);
		state_errors.add(SadFactory.set_expression(statement, expression, 
				arith_sub(expression.get_data_type(), loperand, roperand)));
		state_errors.add(SadFactory.add_operand(statement, expression, COperator.
				arith_add, this.arith_mul(expression.get_data_type(), -2, roperand)));
		state_error = SadFactory.conjunct(statement, state_errors);
		
		this.connect(source, state_error, constraint);
	}
	/**
	 * [x == 0 || y == 0]	--> set_expr(e, 0)
	 * [x != 0 && y != 0]	--> set_expr(e, x * y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	protected void set_arith_mul(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition, condition;
		SadAssertion constraint, state_error;
		
		/* [x == 0 || y == 0] --> set_expr(e, 0) */
		lcondition = this.equal_with(loperand, 0);
		rcondition = this.equal_with(roperand, 0);
		condition = this.logic_ior(lcondition, rcondition);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, sad_expression(0));
		this.connect(source, state_error, constraint);
		
		/* [x != 0 && y != 0] --> set_expr(e, x * y) */
		lcondition = this.not_equals(loperand, 0);
		rcondition = this.not_equals(roperand, 0);
		condition = this.logic_and(lcondition, rcondition);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, 
				this.arith_mul(expression.get_data_type(), loperand, roperand));
		this.connect(source, state_error, constraint);
	}
	/**
	 * [y == 0] 			--> trap()
	 * [x == 0 && y != 0] 	--> set(e, 0)
	 * [x != 0 && y != 0]	--> set(e, x/y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	protected void set_arith_div(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition, condition;
		SadAssertion constraint, state_error;
		
		/* [y = 0] --> trap() */
		condition = this.equal_with(roperand, 0);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.trap_statement(statement);
		this.connect(source, state_error, constraint);
		
		/* [x == 0 && y != 0] --> set(e, 0) */
		lcondition = this.equal_with(loperand, 0);
		rcondition = this.not_equals(roperand, 0);
		condition = this.logic_and(lcondition, rcondition);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(source, state_error, constraint);
		
		/* [x != 0 && y != 0] --> set(e, x/y) */
		lcondition = this.not_equals(loperand, 0);
		rcondition = this.not_equals(roperand, 0);
		condition = this.logic_and(lcondition, rcondition);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.
				arith_div(expression.get_data_type(), loperand, roperand));
		this.connect(source, state_error, constraint);
	}
	/**
	 * [y == 0] 	--> trap()
	 * [x == k * y] --> set_expr(e, 0)
	 * [y != 0]		--> set_expr(e, x % y)
	 * 
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	protected void set_arith_mod(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		
		/* [y == 0] --> trap() */
		condition = this.equal_with(roperand, 0);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.trap_statement(statement);
		this.connect(source, state_error, constraint);
		
		/* [x == k * y] --> set_expr(e, 0) */
		condition = this.equal_with(loperand, arith_mul(expression.
				get_data_type(), any_value(CBasicTypeImpl.int_type), roperand));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(source, state_error, constraint);
		
		/* [y != 0] --> set_expr(e, x % y) */
		condition = this.not_equals(roperand, 0);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, 
				arith_mod(expression.get_data_type(), loperand, roperand));
		this.connect(source, state_error, constraint);
	}
	/**
	 * [x == 0 || y == 0] --> set_expr(e, 0)
	 * [x != 0 && y != 0] --> set_expr(e, x & y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	protected void set_bitws_and(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		
		condition = this.logic_ior(this.equal_with(loperand, 0), this.equal_with(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(source, state_error, constraint);
		
		condition = this.logic_and(this.not_equals(loperand, 0), this.not_equals(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_and(expression.get_data_type(), loperand, roperand));
		this.connect(source, state_error, constraint);
	}
	/**
	 * [x & y != 0] --> set_expr(e, x | y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	protected void set_bitws_ior(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		condition = this.bitws_and(expression.get_data_type(), loperand, roperand);
		constraint = SadFactory.assert_condition(statement, this.not_equals(condition, 0));
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_ior(expression.get_data_type(), loperand, roperand));
		this.connect(source, state_error, constraint);
	}
	/**
	 * [x != 0 && y != 0] --> set_expr(e, x ^ y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	protected void set_bitws_xor(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		condition = this.logic_and(this.not_equals(loperand, 0), this.not_equals(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, 
					expression, this.bitws_xor(expression.get_data_type(), loperand, roperand));
		this.connect(source, state_error, constraint);
	}
	/**
	 * [x == 0 || y >= size] --> set(e, 0)
	 * [x!= 0 && y < size] --> set(e, x << y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	protected void set_bitws_lsh(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition; SadAssertion constraint, state_error;
		
		lcondition = this.equal_with(loperand, 0); 
		rcondition = this.greater_eq(roperand, max_shift_size);
		constraint = SadFactory.assert_condition(statement, this.logic_ior(lcondition, rcondition));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(source, state_error, constraint);
		
		lcondition = this.not_equals(loperand, 0);
		rcondition = this.smaller_tn(roperand, max_shift_size);
		constraint = SadFactory.assert_condition(statement, this.logic_and(lcondition, rcondition));
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_lsh(expression.get_data_type(), loperand, roperand));
		this.connect(source, state_error, constraint);
	}
	/**
	 * [x == 0 || y >= size] --> set(e, 0)
	 * [x!= 0 && y < size] --> set(e, x >> y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	protected void set_bitws_rsh(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition; SadAssertion constraint, state_error;
		
		lcondition = this.equal_with(loperand, 0); 
		rcondition = this.greater_eq(roperand, max_shift_size);
		constraint = SadFactory.assert_condition(statement, this.logic_ior(lcondition, rcondition));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(source, state_error, constraint);
		
		lcondition = this.not_equals(loperand, 0);
		rcondition = this.smaller_tn(roperand, max_shift_size);
		constraint = SadFactory.assert_condition(statement, this.logic_and(lcondition, rcondition));
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_rsh(expression.get_data_type(), loperand, roperand));
		this.connect(source, state_error, constraint);
	}
	/**
	 * [x == 0 || y == 0] --> set_expr(expr, 0)
	 * [x != 0 && y != 0] --> set_expr(expr, 1)
	 */
	protected void set_logic_and(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error; SadExpression condition;
		
		condition = this.logic_ior(this.equal_with(loperand, 0), this.equal_with(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(source, state_error, constraint);
		
		condition = this.logic_and(this.not_equals(loperand, 0), this.not_equals(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(1));
		this.connect(source, state_error, constraint);
	}
	/**
	 * [x != 0 || y != 0] --> set_expr(e, 1)
	 */
	@Override
	protected void set_logic_ior(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error; SadExpression condition;
		condition = this.logic_ior(this.not_equals(loperand, 0), this.not_equals(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(1));
		this.connect(source, state_error, constraint);
	}
	/**
	 * [x > y]  --> set_true
	 * [x <= y] --> set_false
	 */
	protected void set_greater_tn(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error; 
		
		constraint = SadFactory.assert_condition(statement, this.greater_tn(loperand, roperand));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(1));
		this.connect(source, state_error, constraint);
		
		constraint = SadFactory.assert_condition(statement, this.smaller_eq(loperand, roperand));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(source, state_error, constraint);
	}
	/**
	 * [x >= y]  --> set_true
	 * [x < y] --> set_false
	 */
	protected void set_greater_eq(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error; 
		
		constraint = SadFactory.assert_condition(statement, this.greater_eq(loperand, roperand));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(1));
		this.connect(source, state_error, constraint);
		
		constraint = SadFactory.assert_condition(statement, this.smaller_tn(loperand, roperand));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(source, state_error, constraint);
	}
	/**
	 * [x >= y]  --> set_false
	 * [x < y] --> set_true
	 */
	protected void set_smaller_tn(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error; 
		
		constraint = SadFactory.assert_condition(statement, this.greater_eq(loperand, roperand));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(source, state_error, constraint);
		
		constraint = SadFactory.assert_condition(statement, this.smaller_tn(loperand, roperand));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(1));
		this.connect(source, state_error, constraint);
	}
	/**
	 * [x > y] --> set_false
	 * [x <= y] --> set_true
	 */
	protected void set_smaller_eq(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error; 
		
		constraint = SadFactory.assert_condition(statement, this.greater_tn(loperand, roperand));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(source, state_error, constraint);
		
		constraint = SadFactory.assert_condition(statement, this.smaller_eq(loperand, roperand));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(1));
		this.connect(source, state_error, constraint);
	}
	/**
	 * [x == y] --> set_true
	 * ....
	 */
	protected void set_equal_with(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error; 
		
		constraint = SadFactory.assert_condition(statement, this.equal_with(loperand, roperand));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(1));
		this.connect(source, state_error, constraint);
		
		constraint = SadFactory.assert_condition(statement, this.not_equals(loperand, roperand));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(source, state_error, constraint);
	}
	/**
	 * [x == y] --> set_false
	 * ....
	 */
	protected void set_not_equals(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error; 
		
		constraint = SadFactory.assert_condition(statement, this.equal_with(loperand, roperand));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(source, state_error, constraint);
		
		constraint = SadFactory.assert_condition(statement, this.not_equals(loperand, roperand));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(1));
		this.connect(source, state_error, constraint);
	}
	
	/* cmp_operator */
	protected void cmp_arith_add(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception { /* equivalent mutations */ }
	/**
	 * [y != 0] --> trap
	 */
	protected void cmp_arith_sub(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint;
		constraint = SadFactory.assert_condition(statement, this.not_equals(roperand, 0));
		this.connect(source, SadFactory.trap_statement(statement), constraint);
	}
	/**
	 * [x != 0 || y != 0]
	 */
	protected void cmp_arith_mul(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint;
		constraint = SadFactory.assert_condition(statement, this.logic_ior(
				this.not_equals(loperand, 0), this.not_equals(roperand, 0)));
		this.connect(source, SadFactory.trap_statement(statement), constraint);
	}
	/**
	 * [true] --> trap()
	 */
	protected void cmp_arith_div(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		this.connect(source, SadFactory.trap_statement(statement));
	}
	/**
	 * --> trap()
	 */
	protected void cmp_arith_mod(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		this.connect(source, SadFactory.trap_statement(statement));
	}
	/**
	 * [x != 0 || y != 0]
	 */
	protected void cmp_bitws_and(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint;
		constraint = SadFactory.assert_condition(statement, this.logic_ior(
				this.not_equals(loperand, 0), this.not_equals(roperand, 0)));
		this.connect(source, SadFactory.trap_statement(statement), constraint);
	}
	/**
	 * [x & y != 0]
	 */
	@Override
	protected void cmp_bitws_ior(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint; SadExpression condition;
		condition = this.bitws_and(expression.get_data_type(), loperand, roperand);
		condition = this.not_equals(condition, this.sad_expression(0));
		constraint = SadFactory.assert_condition(statement, condition);
		this.connect(source, SadFactory.trap_statement(statement), constraint);
	}
	/**
	 * [x != 0 && y != 0]
	 */
	protected void cmp_bitws_xor(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		condition = this.logic_and(this.not_equals(loperand, 0), this.not_equals(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.trap_statement(statement);
		this.connect(source, state_error, constraint);
	}
	/**
	 * [x != 0 || y != 0]
	 */
	protected void cmp_bitws_lsh(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint;
		constraint = SadFactory.assert_condition(statement, this.logic_ior(
				this.not_equals(loperand, 0), this.not_equals(roperand, 0)));
		this.connect(source, SadFactory.trap_statement(statement), constraint);
	}
	/**
	 * [x != 0 || y != 0]
	 */
	protected void cmp_bitws_rsh(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint;
		constraint = SadFactory.assert_condition(statement, this.logic_ior(
				this.not_equals(loperand, 0), this.not_equals(roperand, 0)));
		this.connect(source, SadFactory.trap_statement(statement), constraint);
	}
	/**
	 * [x != 0 || y != 0]
	 */
	protected void cmp_logic_and(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint;
		constraint = SadFactory.assert_condition(statement, this.logic_ior(
				this.not_equals(loperand, 0), this.not_equals(roperand, 0)));
		this.connect(source, SadFactory.trap_statement(statement), constraint);
	}
	/**
	 * [x != 0 || y != 0]
	 */
	protected void cmp_logic_ior(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint;
		constraint = SadFactory.assert_condition(statement, this.logic_ior(
				this.not_equals(loperand, 0), this.not_equals(roperand, 0)));
		this.connect(source, SadFactory.trap_statement(statement), constraint);
	}
	/**
	 * [x != 0 || y != 0]
	 */
	protected void cmp_greater_tn(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint;
		constraint = SadFactory.assert_condition(statement, this.logic_ior(
				this.not_equals(loperand, 0), this.not_equals(roperand, 0)));
		this.connect(source, SadFactory.trap_statement(statement), constraint);
	}
	/**
	 * [true]
	 */
	protected void cmp_greater_eq(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		this.connect(source, SadFactory.trap_statement(statement));
	}
	/**
	 * [x != 0 || y != 0]
	 */
	protected void cmp_smaller_tn(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint;
		constraint = SadFactory.assert_condition(statement, this.logic_ior(
				this.not_equals(loperand, 0), this.not_equals(roperand, 0)));
		this.connect(source, SadFactory.trap_statement(statement), constraint);
	}
	protected void cmp_smaller_eq(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		this.connect(source, SadFactory.trap_statement(statement));
	}
	protected void cmp_equal_with(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		this.connect(source, SadFactory.trap_statement(statement));
	}
	/**
	 * [x != 0 || y != 0]
	 */
	protected void cmp_not_equals(SadVertex source, CirTree tree, CirStatement statement, CirExpression expression,
			CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint;
		constraint = SadFactory.assert_condition(statement, this.logic_ior(
				this.not_equals(loperand, 0), this.not_equals(roperand, 0)));
		this.connect(source, SadFactory.trap_statement(statement), constraint);
	}

}
