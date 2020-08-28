package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect;

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

public class OBANSadInfection extends SadInfection {

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
			if(operator == COperator.bit_and) {
				switch(parameter) {
				case arith_add: this.bitws_and_to_arith_add(reach_node, statement, expression, loperand, roperand);	break;
				case arith_sub:	this.bitws_and_to_arith_sub(reach_node, statement, expression, loperand, roperand);	break;
				case arith_mul:	this.bitws_and_to_arith_mul(reach_node, statement, expression, loperand, roperand);	break;
				case arith_div:	this.bitws_and_to_arith_div(reach_node, statement, expression, loperand, roperand);	break;
				case arith_mod:	this.bitws_and_to_arith_mod(reach_node, statement, expression, loperand, roperand);	break;
				default: throw new IllegalArgumentException("Invalid: " + parameter);
				}
			}
			else if(operator == COperator.bit_or) {
				switch(parameter) {
				case arith_add:	break;
				case arith_sub:	break;
				case arith_mul:	break;
				case arith_div:	break;
				case arith_mod:	break;
				default: throw new IllegalArgumentException("Invalid: " + parameter);
				}
			}
			else if(operator == COperator.bit_xor) {
				switch(parameter) {
				case arith_add:	break;
				case arith_sub:	break;
				case arith_mul:	break;
				case arith_div:	break;
				case arith_mod:	break;
				default: throw new IllegalArgumentException("Invalid: " + parameter);
				}
			}
			else if(operator == COperator.left_shift) {
				switch(parameter) {
				case arith_add:	break;
				case arith_sub:	break;
				case arith_mul:	break;
				case arith_div:	break;
				case arith_mod:	break;
				default: throw new IllegalArgumentException("Invalid: " + parameter);
				}
			}
			else if(operator == COperator.righ_shift) {
				switch(parameter) {
				case arith_add:	break;
				case arith_sub:	break;
				case arith_mul:	break;
				case arith_div:	break;
				case arith_mod:	break;
				default: throw new IllegalArgumentException("Invalid: " + parameter);
				}
			}
			else {
				throw new IllegalArgumentException("Invalid operator: " + operator);
			}
		}
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
	private void bitws_and_to_arith_add(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		condition = logic_ior(not_equals(loperand, 0), not_equals(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, 
				this.arith_add(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0 && x == y] --> set_expr(e, 0)
	 * [x != 0 && x != y] --> set_expr(e, x - y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_and_to_arith_sub(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		
		condition = logic_and(not_equals(loperand, 0), equal_with(loperand, roperand));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		condition = logic_and(not_equals(loperand, 0), not_equals(loperand, roperand));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, 
				this.arith_sub(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0 && y != 0] --> set_expr(e, x * y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_and_to_arith_mul(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		condition = logic_and(not_equals(loperand, 0), not_equals(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, 
				this.arith_mul(expression.get_data_type(), loperand, roperand));
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
	private void bitws_and_to_arith_div(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		
		condition = this.equal_with(roperand, 0);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.trap_statement(statement);
		this.connect(reach_node, state_error, constraint);
		
		condition = logic_and(not_equals(loperand, 0), not_equals(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, 
				this.arith_div(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [y == 0] --> trap()
	 * [x != 0 && y != 0] --> set_expr(e, x % y)
	 * [x == k * y && y != 0] --> set_expr(e, 0)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_and_to_arith_mod(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		
		condition = this.equal_with(roperand, 0);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.trap_statement(statement);
		this.connect(reach_node, state_error, constraint);
		
		condition = logic_and(not_equals(loperand, 0), not_equals(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, 
				this.arith_mod(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
		
		condition = this.equal_with(loperand, this.arith_mul(expression.
				get_data_type(), this.any_value(CBasicTypeImpl.int_type), roperand));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
	}
	
	/**
	 * [x & y != 0] --> set_expr(e, x + y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_ior_to_arith_add(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		condition = this.not_equals(this.bitws_and(expression.get_data_type(), loperand, roperand), 0);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, 
				expression, this.arith_add(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [y != 0 && x == y] --> set_expr(e, 0)
	 * [y != 0 && x != y] --> set_expr(e, x - y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_ior_to_arith_sub(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		
		condition = this.logic_and(this.not_equals(roperand, 0), this.equal_with(loperand, roperand));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		condition = this.logic_and(this.not_equals(roperand, 0), this.not_equals(loperand, roperand));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, 
				expression, this.arith_sub(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x == 0 || y == 0] --> set_expr(e, 0)
	 * [x != 0 && y != 0] --> set_expr(e, x * y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_ior_to_arith_mul(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		
		condition = this.logic_ior(this.equal_with(loperand, 0), this.equal_with(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		condition = this.logic_and(this.not_equals(loperand, 0), this.not_equals(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, 
				expression, this.arith_mul(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [y == 0] --> trap()
	 * [y != 0 && x == 0] --> set_expr(e, 0)
	 * [y != 0 && x != 0] --> set_expr(e, x / y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_ior_to_arith_div(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		
		condition = this.equal_with(roperand, 0);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.trap_statement(statement);
		this.connect(reach_node, state_error, constraint);
		
		condition = this.logic_and(this.equal_with(loperand, 0), this.not_equals(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		condition = this.logic_and(this.not_equals(loperand, 0), this.not_equals(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, 
				expression, this.arith_div(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	
	
}
