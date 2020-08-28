package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadInfection;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OAANSadInfection extends SadInfection {
	
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
	private void arith_add_to_arith_sub(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		List<SadAssertion> state_errors = new ArrayList<SadAssertion>();
		
		condition = this.not_equals(roperand, 0);
		constraint = SadFactory.assert_condition(statement, condition);
		state_errors.add(SadFactory.set_expression(statement, expression, 
				arith_sub(expression.get_data_type(), loperand, roperand)));
		state_errors.add(SadFactory.add_operand(statement, expression, COperator.
				arith_add, this.arith_mul(expression.get_data_type(), -2, roperand)));
		state_error = SadFactory.conjunct(statement, state_errors);
		
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [roperand != 0]
	 * 	-->
	 * 	assert#stmt:set_expr(expression, x + y)	&&
	 * 	assert#stmt:add_operand(expression, +, 2 * y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_sub_to_arith_add(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		List<SadAssertion> state_errors = new ArrayList<SadAssertion>();
		
		condition = this.not_equals(roperand, 0);
		constraint = SadFactory.assert_condition(statement, condition);
		state_errors.add(SadFactory.set_expression(statement, expression, 
				arith_add(expression.get_data_type(), loperand, roperand)));
		state_errors.add(SadFactory.add_operand(statement, expression, COperator.
				arith_add, this.arith_mul(expression.get_data_type(), 2, roperand)));
		state_error = SadFactory.conjunct(statement, state_errors);
		
		this.connect(reach_node, state_error, constraint);
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
	private void arith_add_to_arith_mul(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition, condition;
		SadAssertion constraint, state_error;
		
		/* [x == 0 || y == 0] --> set_expr(e, 0) */
		lcondition = this.equal_with(loperand, 0);
		rcondition = this.equal_with(roperand, 0);
		condition = this.logic_ior(lcondition, rcondition);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		/* [x != 0 && y != 0] --> set_expr(e, x * y) */
		lcondition = this.not_equals(loperand, 0);
		rcondition = this.not_equals(roperand, 0);
		condition = this.logic_and(lcondition, rcondition);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, 
				this.arith_mul(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0 || y != 0] --> set_expr(e, x + y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_mul_to_arith_add(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition, condition;
		SadAssertion constraint, state_error;
		
		/* [x != 0 || y != 0] --> set_expr(e, x + y) */
		lcondition = this.not_equals(loperand, 0);
		rcondition = this.not_equals(roperand, 0);
		condition = this.logic_ior(lcondition, rcondition);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, 
				arith_add(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
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
	private void arith_add_to_arith_div(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition, condition;
		SadAssertion constraint, state_error;
		
		/* [y = 0] --> trap() */
		condition = this.equal_with(roperand, 0);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.trap_statement(statement);
		this.connect(reach_node, state_error, constraint);
		
		/* [x == 0 && y != 0] --> set(e, 0) */
		lcondition = this.equal_with(loperand, 0);
		rcondition = this.not_equals(roperand, 0);
		condition = this.logic_and(lcondition, rcondition);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		/* [x != 0 && y != 0] --> set(e, x/y) */
		lcondition = this.not_equals(loperand, 0);
		rcondition = this.not_equals(roperand, 0);
		condition = this.logic_and(lcondition, rcondition);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.
				arith_div(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [true] --> set_expr(e, x + y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_div_to_arith_add(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		constraint = SadFactory.assert_condition(statement, sad_expression(true));
		state_error = SadFactory.set_expression(statement, expression, 
				this.arith_add(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
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
	private void arith_add_to_arith_mod(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		
		/* [y == 0] --> trap() */
		condition = this.equal_with(roperand, 0);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.trap_statement(statement);
		this.connect(reach_node, state_error, constraint);
		
		/* [x == k * y] --> set_expr(e, 0) */
		condition = this.equal_with(loperand, arith_mul(expression.
				get_data_type(), any_value(CBasicTypeImpl.int_type), roperand));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		/* [y != 0] --> set_expr(e, x % y) */
		condition = this.not_equals(roperand, 0);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, 
				arith_mod(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [true] --> set_expr(e, x + y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_mod_to_arith_add(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		constraint = SadFactory.assert_condition(statement, sad_expression(true));
		state_error = SadFactory.set_expression(statement, expression, 
				this.arith_add(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x == 0 || y == 0] --> set_expr(e, 0)
	 * [x != 0 && y != 0] --> set_expr(e, x * y)
	 * 
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_sub_to_arith_mul(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition, condition;
		SadAssertion constraint, state_error;
		
		lcondition = this.equal_with(loperand, 0);
		rcondition = this.equal_with(roperand, 0);
		condition = this.logic_ior(lcondition, rcondition);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		lcondition = this.not_equals(loperand, 0);
		rcondition = this.not_equals(roperand, 0);
		condition = this.logic_and(lcondition, rcondition);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, 
				this.arith_mul(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x == y] --> set_expr(e, 0)
	 * [x != y] --> set_expr(e, x - y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_mul_to_arith_sub(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		
		constraint = SadFactory.assert_condition(statement, this.equal_with(loperand, roperand));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		constraint = SadFactory.assert_condition(statement, not_equals(loperand, roperand));
		state_error = SadFactory.set_expression(statement, expression, 
				this.arith_sub(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [y == 0] --> trap()
	 * [x == 0 && y != 0] --> set_expr(e, 0)
	 * [x != 0 && y != 0] --> set_expr(e, x / y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_sub_to_arith_div(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition, condition;
		SadAssertion constraint, state_error;
		
		/* [y == 0] --> trap() */
		condition = this.equal_with(roperand, 0);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.trap_statement(statement);
		this.connect(reach_node, state_error, constraint);
		
		/* [x == 0 && y != 0] --> set_expr(e, 0) */
		lcondition = this.equal_with(loperand, 0);
		rcondition = this.not_equals(roperand, 0);
		condition = this.logic_and(lcondition, rcondition);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		/* [x != 0 && y != 0] --> set_expr(e, x / y) */
		lcondition = this.not_equals(loperand, 0);
		rcondition = this.not_equals(roperand, 0);
		condition = this.logic_and(lcondition, rcondition);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, 
				this.arith_div(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x == y] --> set_expr(e, 0) && add_operand(e, +, -1)
	 * [x != y] --> set_expr(e, x - y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_div_to_arith_sub(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error; 
		List<SadAssertion> state_errors = new ArrayList<SadAssertion>();
		
		constraint = SadFactory.assert_condition(statement, this.equal_with(loperand, roperand));
		state_errors.add(SadFactory.set_expression(statement, expression, this.sad_expression(0)));
		state_errors.add(SadFactory.add_operand(statement, expression, COperator.arith_add, this.sad_expression(-1)));
		state_error = SadFactory.conjunct(statement, state_errors);
		this.connect(reach_node, state_error, constraint);
		
		constraint = SadFactory.assert_condition(statement, not_equals(loperand, roperand));
		state_error = SadFactory.set_expression(statement, expression, 
				this.arith_sub(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [y == 0] --> trap()
	 * [x < y || x > 2 * y] --> set_expr(e, x % y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_sub_to_arith_mod(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition, condition;
		SadAssertion constraint, state_error;
		
		/* [y == 0] --> trap() */
		condition = this.equal_with(roperand, 0);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.trap_statement(statement);
		this.connect(reach_node, state_error, constraint);
		
		/* [x < y || x > 2 * y] --> set_expr(e, x % y) */
		lcondition = this.smaller_tn(loperand, roperand);
		rcondition = this.greater_tn(loperand, this.arith_mul(expression.get_data_type(), 2, roperand));
		constraint = SadFactory.assert_condition(statement, this.logic_ior(lcondition, rcondition));
		state_error = SadFactory.set_expression(
				statement, expression, this.arith_mod(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x < y || x > 2 * y] --> set_expr(e, x % y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_mod_to_arith_sub(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition; SadAssertion constraint, state_error;
		/* [x < y || x > 2 * y] --> set_expr(e, x - y) */
		lcondition = this.smaller_tn(loperand, roperand);
		rcondition = this.greater_tn(loperand, this.arith_mul(expression.get_data_type(), 2, roperand));
		constraint = SadFactory.assert_condition(statement, this.logic_ior(lcondition, rcondition));
		state_error = SadFactory.set_expression(
				statement, expression, this.arith_sub(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [y == 0] --> trap()
	 * [x != 0 && y != 0] --> set_expr(e, x / y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_mul_to_arith_div(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition, condition;
		SadAssertion constraint, state_error;
		
		/* [y == 0] --> trap() */
		constraint = SadFactory.assert_condition(statement, this.equal_with(roperand, 0));
		state_error = SadFactory.trap_statement(statement);
		this.connect(reach_node, state_error, constraint);
		
		/* [x != 0 && y != 0] --> set_expr(e, x / y) */
		lcondition = this.not_equals(loperand, 0);
		rcondition = this.not_equals(roperand, 0);
		condition = this.logic_and(lcondition, rcondition);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, 
				arith_div(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0] --> set(x * y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_div_to_arith_mul(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		constraint = SadFactory.assert_condition(statement, this.not_equals(loperand, 0));
		state_error = SadFactory.set_expression(statement, 
				expression, this.arith_mul(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [y == 0] --> trap()
	 * [x != 0 && y != 0] --> set_expr(e, x % y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_mul_to_arith_mod(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition; SadAssertion constraint, state_error;
		
		constraint = SadFactory.assert_condition(statement, this.equal_with(roperand, 0));
		state_error = SadFactory.trap_statement(statement);
		this.connect(reach_node, state_error, constraint);
		
		lcondition = this.not_equals(loperand, 0); rcondition = this.not_equals(roperand, 0);
		constraint = SadFactory.assert_condition(statement, this.logic_and(lcondition, rcondition));
		state_error = SadFactory.set_expression(statement, 
						expression, this.arith_mod(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0] --> set_expr(x * y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_mod_to_arith_mul(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		constraint = SadFactory.assert_condition(statement, this.not_equals(loperand, 0));
		state_error = SadFactory.set_expression(statement, 
				expression, this.arith_mul(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0] --> set_expr(x % y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_div_to_arith_mod(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		constraint = SadFactory.assert_condition(statement, this.not_equals(loperand, 0));
		state_error = SadFactory.set_expression(statement, 
				expression, this.arith_mod(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0] --> set_expr(x / y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_mod_to_arith_div(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		constraint = SadFactory.assert_condition(statement, this.not_equals(loperand, 0));
		state_error = SadFactory.set_expression(statement, 
				expression, this.arith_div(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	
	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		/* declarations */
		AstExpression location = (AstExpression) mutation.get_location();
		CirExpression expression = this.find_result(tree, location);
		CirStatement statement = expression.statement_of();
		CirExpression loperand = ((CirComputeExpression) expression).get_operand(0);
		CirExpression roperand = ((CirComputeExpression) expression).get_operand(1);
		COperator operator = ((CirComputeExpression) expression).get_operator();
		COperator parameter = (COperator) mutation.get_parameter();
		
		if(statement != null) {
			if(operator == COperator.arith_add) {
				switch(parameter) {
				case arith_add: break;
				case arith_sub: this.arith_add_to_arith_sub(reach_node, statement, expression, loperand, roperand);	break;
				case arith_mul:	this.arith_add_to_arith_mul(reach_node, statement, expression, loperand, roperand);	break;
				case arith_div:	this.arith_add_to_arith_div(reach_node, statement, expression, loperand, roperand);	break;
				case arith_mod:	this.arith_add_to_arith_mod(reach_node, statement, expression, loperand, roperand);	break;
				default: throw new IllegalArgumentException("Unknown parameter: " + parameter);
				}
			}
			else if(operator == COperator.arith_sub) {
				switch(parameter) {
				case arith_add: this.arith_sub_to_arith_add(reach_node, statement, expression, loperand, roperand);	break;
				case arith_sub:	break;
				case arith_mul: this.arith_sub_to_arith_mul(reach_node, statement, expression, loperand, roperand);	break;
				case arith_div: this.arith_sub_to_arith_div(reach_node, statement, expression, loperand, roperand);	break;
				case arith_mod: this.arith_sub_to_arith_mod(reach_node, statement, expression, loperand, roperand);	break;
				default: throw new IllegalArgumentException("Unknown parameter: " + parameter);
				}
			}
			else if(operator == COperator.arith_mul) {
				switch(parameter) {
				case arith_add: this.arith_mul_to_arith_add(reach_node, statement, expression, loperand, roperand);	break;
				case arith_sub: this.arith_mul_to_arith_sub(reach_node, statement, expression, loperand, roperand);	break;
				case arith_mul:	break;
				case arith_div: this.arith_mul_to_arith_div(reach_node, statement, expression, loperand, roperand);	break;
				case arith_mod: this.arith_mul_to_arith_mod(reach_node, statement, expression, loperand, roperand);	break;
				default: throw new IllegalArgumentException("Unknown parameter: " + parameter);
				}
			}
			else if(operator == COperator.arith_div) {
				switch(parameter) {
				case arith_add: this.arith_div_to_arith_add(reach_node, statement, expression, loperand, roperand);	break;
				case arith_sub: this.arith_div_to_arith_sub(reach_node, statement, expression, loperand, roperand);	break;
				case arith_mul: this.arith_div_to_arith_mul(reach_node, statement, expression, loperand, roperand);	break;
				case arith_div:	break;
				case arith_mod: this.arith_div_to_arith_mod(reach_node, statement, expression, loperand, roperand);	break;
				default: throw new IllegalArgumentException("Unknown parameter: " + parameter);
				}
			}
			else if(operator == COperator.arith_mod) {
				switch(parameter) {
				case arith_add: this.arith_mod_to_arith_add(reach_node, statement, expression, loperand, roperand);	break;
				case arith_sub: this.arith_mod_to_arith_sub(reach_node, statement, expression, loperand, roperand);	break;
				case arith_mul: this.arith_mod_to_arith_mul(reach_node, statement, expression, loperand, roperand);	break;
				case arith_div: this.arith_mod_to_arith_div(reach_node, statement, expression, loperand, roperand);	break;
				case arith_mod:	break;
				default: throw new IllegalArgumentException("Unknown parameter: " + parameter);
				}
			}
			else {
				throw new IllegalArgumentException("Invalid operator: " + operator);
			}
		}
	}

}
