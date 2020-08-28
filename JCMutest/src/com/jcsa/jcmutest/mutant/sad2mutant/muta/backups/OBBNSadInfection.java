package com.jcsa.jcmutest.mutant.sad2mutant.muta.backups;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.infect.SadInfection;
import com.jcsa.jcparse.lang.astree.expr.AstExpression;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OBBNSadInfection extends SadInfection {
	
	/**
	 * [x & y != 0] --> set_expr(e, x | y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_and_to_bitws_ior(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		condition = this.bitws_and(expression.get_data_type(), loperand, roperand);
		constraint = SadFactory.assert_condition(statement, not_equals(condition, 0));
		state_error = SadFactory.set_expression(statement, 
				expression, bitws_ior(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x & y != 0] --> set_expr(e, x & y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_ior_to_bitws_and(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		condition = this.bitws_and(expression.get_data_type(), loperand, roperand);
		constraint = SadFactory.assert_condition(statement, not_equals(condition, 0));
		state_error = SadFactory.set_expression(statement, 
				expression, bitws_and(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0 || y != 0] --> set_expr(e, x ^ y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_and_to_bitws_xor(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition; SadAssertion constraint, state_error;
		lcondition = this.not_equals(loperand, 0); rcondition = this.not_equals(roperand, 0);
		constraint = SadFactory.assert_condition(statement, this.logic_ior(lcondition, rcondition));
		state_error = SadFactory.set_expression(
				statement, expression, this.bitws_xor(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0 || y != 0] --> set_expr(e, x & y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_xor_to_bitws_and(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition; SadAssertion constraint, state_error;
		lcondition = this.not_equals(loperand, 0); rcondition = this.not_equals(roperand, 0);
		constraint = SadFactory.assert_condition(statement, this.logic_ior(lcondition, rcondition));
		state_error = SadFactory.set_expression(
				statement, expression, this.bitws_and(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0 && y < size] --> set_expr(e, x << y)
	 * [x != 0 && y >= size] --> set_expr(e, 0)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_and_to_bitws_lsh(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition; SadAssertion constraint, state_error;
		
		lcondition = not_equals(loperand, 0); rcondition = smaller_tn(roperand, max_shift_size);
		constraint = SadFactory.assert_condition(statement, logic_and(lcondition, rcondition));
		state_error = SadFactory.set_expression(statement, 
					expression, this.bitws_lsh(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
		
		lcondition = not_equals(loperand, 0); rcondition = greater_eq(roperand, max_shift_size);
		constraint = SadFactory.assert_condition(statement, logic_and(lcondition, rcondition));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0 && y != 0] --> set_expr(e, x && y)
	 * [x != 0 && y == 0] --> set_expr(e, 0)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_lsh_to_bitws_and(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition; SadAssertion constraint, state_error;
		
		lcondition = this.not_equals(loperand, 0); rcondition = this.not_equals(roperand, 0);
		constraint = SadFactory.assert_condition(statement, logic_and(lcondition, rcondition));
		state_error = SadFactory.set_expression(statement, 
					expression, this.bitws_and(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
		
		lcondition = this.not_equals(loperand, 0); rcondition = this.equal_with(roperand, 0);
		constraint = SadFactory.assert_condition(statement, logic_and(lcondition, rcondition));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0 && y < size] --> set_expr(e, x >> y)
	 * [x != 0 && y >= size] --> set_expr(e, 0)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_and_to_bitws_rsh(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition; SadAssertion constraint, state_error;
		
		lcondition = not_equals(loperand, 0); rcondition = smaller_tn(roperand, max_shift_size);
		constraint = SadFactory.assert_condition(statement, logic_and(lcondition, rcondition));
		state_error = SadFactory.set_expression(statement, 
					expression, this.bitws_rsh(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
		
		lcondition = not_equals(loperand, 0); rcondition = greater_eq(roperand, max_shift_size);
		constraint = SadFactory.assert_condition(statement, logic_and(lcondition, rcondition));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0 && y != 0] --> set_expr(e, x && y)
	 * [x != 0 && y == 0] --> set_expr(e, 0)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_rsh_to_bitws_and(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition; SadAssertion constraint, state_error;
		
		lcondition = this.not_equals(loperand, 0); rcondition = this.not_equals(roperand, 0);
		constraint = SadFactory.assert_condition(statement, logic_and(lcondition, rcondition));
		state_error = SadFactory.set_expression(statement, 
					expression, this.bitws_and(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
		
		lcondition = this.not_equals(loperand, 0); rcondition = this.equal_with(roperand, 0);
		constraint = SadFactory.assert_condition(statement, logic_and(lcondition, rcondition));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != y] --> set_expr(e, x ^ y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_ior_to_bitws_xor(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		constraint = SadFactory.assert_condition(statement, this.not_equals(loperand, roperand));
		state_error = SadFactory.set_expression(statement, 
					expression, this.bitws_xor(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != y] --> set_expr(e, x | y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_xor_to_bitws_ior(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		constraint = SadFactory.assert_condition(statement, this.not_equals(loperand, roperand));
		state_error = SadFactory.set_expression(statement, 
					expression, this.bitws_ior(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x == 0] --> set_expr(e, 0)
	 * [y < max_size] --> set_expr(e, x << y)
	 * [y >= max_size]--> set_expr(e, 0)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_ior_to_bitws_lsh(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		
		constraint = SadFactory.assert_condition(statement, this.smaller_tn(roperand, max_shift_size));
		state_error = SadFactory.set_expression(
				statement, expression, this.bitws_lsh(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
		
		constraint = SadFactory.assert_condition(statement, this.equal_with(loperand, 0));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		constraint = SadFactory.assert_condition(statement, this.greater_eq(roperand, max_shift_size));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [y < max_size] --> set_expr(e, x << y)
	 * [y >= max_size]--> set_expr(e, 0)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_ior_to_bitws_rsh(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		
		constraint = SadFactory.assert_condition(statement, this.smaller_tn(roperand, max_shift_size));
		state_error = SadFactory.set_expression(
				statement, expression, this.bitws_rsh(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
		
		constraint = SadFactory.assert_condition(statement, this.equal_with(loperand, 0));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		constraint = SadFactory.assert_condition(statement, this.greater_eq(roperand, max_shift_size));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [true] --> set_expr(e, x | y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_lsh_to_bitws_ior(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		constraint = SadFactory.assert_condition(statement, this.sad_expression(true));
		state_error = SadFactory.set_expression(statement, 
				expression, bitws_ior(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [true] --> set_expr(e, x | y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_rsh_to_bitws_ior(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		constraint = SadFactory.assert_condition(statement, this.sad_expression(true));
		state_error = SadFactory.set_expression(statement, 
				expression, bitws_ior(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x == 0] --> set_expr(e, 0)
	 * [y < max_size] --> set_expr(e, x << y)
	 * [y >= max_size]--> set_expr(e, 0)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_xor_to_bitws_lsh(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		
		constraint = SadFactory.assert_condition(statement, this.smaller_tn(roperand, max_shift_size));
		state_error = SadFactory.set_expression(
				statement, expression, this.bitws_lsh(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
		
		constraint = SadFactory.assert_condition(statement, this.equal_with(loperand, 0));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		constraint = SadFactory.assert_condition(statement, this.greater_eq(roperand, max_shift_size));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [y < max_size] --> set_expr(e, x >> y)
	 * [y >= max_size]--> set_expr(e, 0)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_xor_to_bitws_rsh(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		
		constraint = SadFactory.assert_condition(statement, this.smaller_tn(roperand, max_shift_size));
		state_error = SadFactory.set_expression(
				statement, expression, this.bitws_rsh(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
		
		constraint = SadFactory.assert_condition(statement, this.equal_with(loperand, 0));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		constraint = SadFactory.assert_condition(statement, this.greater_eq(roperand, max_shift_size));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [true] --> set_expr(e, x ^ y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_lsh_to_bitws_xor(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		constraint = SadFactory.assert_condition(statement, this.sad_expression(true));
		state_error = SadFactory.set_expression(statement, 
				expression, bitws_xor(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [true] --> set_expr(e, x ^ y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_rsh_to_bitws_xor(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		constraint = SadFactory.assert_condition(statement, this.sad_expression(true));
		state_error = SadFactory.set_expression(statement, 
				expression, bitws_xor(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0 && y != 0] --> set_expr(e, x >> y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_lsh_to_bitws_rsh(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		condition = this.logic_and(this.not_equals(loperand, 0), this.not_equals(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, 
					expression, this.bitws_rsh(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0 && y != 0] --> set_expr(e, x >> y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void bitws_rsh_to_bitws_lsh(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		condition = this.logic_and(this.not_equals(loperand, 0), this.not_equals(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, 
					expression, this.bitws_lsh(expression.get_data_type(), loperand, roperand));
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
			if(operator == COperator.bit_and) {
				switch(parameter) {
				case bit_and:		break;
				case bit_or:		this.bitws_and_to_bitws_ior(reach_node, statement, expression, loperand, roperand); break;
				case bit_xor:		this.bitws_and_to_bitws_xor(reach_node, statement, expression, loperand, roperand); break;
				case left_shift:	this.bitws_and_to_bitws_lsh(reach_node, statement, expression, loperand, roperand); break;
				case righ_shift:	this.bitws_and_to_bitws_rsh(reach_node, statement, expression, loperand, roperand); break;
				default: throw new IllegalArgumentException("Invalid parameter: " + parameter);
				}
			}
			else if(operator == COperator.bit_or) {
				switch(parameter) {
				case bit_and:		this.bitws_ior_to_bitws_and(reach_node, statement, expression, loperand, roperand); break;
				case bit_or:		break;
				case bit_xor:		this.bitws_ior_to_bitws_xor(reach_node, statement, expression, loperand, roperand); break;
				case left_shift:	this.bitws_ior_to_bitws_lsh(reach_node, statement, expression, loperand, roperand); break;
				case righ_shift:	this.bitws_ior_to_bitws_rsh(reach_node, statement, expression, loperand, roperand); break;
				default: throw new IllegalArgumentException("Invalid parameter: " + parameter);
				}
			}
			else if(operator == COperator.bit_xor) {
				switch(parameter) {
				case bit_and:		this.bitws_xor_to_bitws_and(reach_node, statement, expression, loperand, roperand); break;
				case bit_or:		this.bitws_xor_to_bitws_ior(reach_node, statement, expression, loperand, roperand); break;
				case bit_xor:		break;
				case left_shift:	this.bitws_xor_to_bitws_lsh(reach_node, statement, expression, loperand, roperand); break;
				case righ_shift:	this.bitws_xor_to_bitws_rsh(reach_node, statement, expression, loperand, roperand); break;
				default: throw new IllegalArgumentException("Invalid parameter: " + parameter);
				}
			}
			else if(operator == COperator.left_shift) {
				switch(parameter) {
				case bit_and:		this.bitws_lsh_to_bitws_and(reach_node, statement, expression, loperand, roperand); break;
				case bit_or:		this.bitws_lsh_to_bitws_ior(reach_node, statement, expression, loperand, roperand); break;
				case bit_xor:		this.bitws_lsh_to_bitws_xor(reach_node, statement, expression, loperand, roperand); break;
				case left_shift:	break;
				case righ_shift:	this.bitws_lsh_to_bitws_rsh(reach_node, statement, expression, loperand, roperand); break;
				default: throw new IllegalArgumentException("Invalid parameter: " + parameter);
				}
			}
			else if(operator == COperator.righ_shift) {
				switch(parameter) {
				case bit_and:		this.bitws_rsh_to_bitws_and(reach_node, statement, expression, loperand, roperand); break;
				case bit_or:		this.bitws_rsh_to_bitws_ior(reach_node, statement, expression, loperand, roperand); break;
				case bit_xor:		this.bitws_rsh_to_bitws_xor(reach_node, statement, expression, loperand, roperand); break;
				case left_shift:	this.bitws_rsh_to_bitws_lsh(reach_node, statement, expression, loperand, roperand); break;
				case righ_shift:	break;
				default: throw new IllegalArgumentException("Invalid parameter: " + parameter);
				}
			}
			else {
				throw new IllegalArgumentException("Invalid operator: " + operator);
			}
		}
	}

}
