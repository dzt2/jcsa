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
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OABNSadInfection extends SadInfection {
	
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
				case bit_and:		this.arith_add_to_bitws_and(reach_node, statement, expression, loperand, roperand); break;
				case bit_or:		this.arith_add_to_bitws_ior(reach_node, statement, expression, loperand, roperand); break;
				case bit_xor:		this.arith_add_to_bitws_xor(reach_node, statement, expression, loperand, roperand); break;
				case left_shift:	this.arith_add_to_bitws_lsh(reach_node, statement, expression, loperand, roperand); break;
				case righ_shift:	this.arith_add_to_bitws_rsh(reach_node, statement, expression, loperand, roperand); break;
				default: throw new IllegalArgumentException("Invalid: " + parameter);
				}
			}
			else if(operator == COperator.arith_sub) {
				switch(parameter) {
				case bit_and:		this.arith_sub_to_bitws_and(reach_node, statement, expression, loperand, roperand); break;
				case bit_or:		this.arith_sub_to_bitws_ior(reach_node, statement, expression, loperand, roperand); break;
				case bit_xor:		this.arith_sub_to_bitws_xor(reach_node, statement, expression, loperand, roperand); break;
				case left_shift:	this.arith_sub_to_bitws_lsh(reach_node, statement, expression, loperand, roperand); break;
				case righ_shift:	this.arith_sub_to_bitws_rsh(reach_node, statement, expression, loperand, roperand); break;
				default: throw new IllegalArgumentException("Invalid: " + parameter);
				}
			}
			else if(operator == COperator.arith_mul) {
				switch(parameter) {
				case bit_and:		this.arith_mul_to_bitws_and(reach_node, statement, expression, loperand, roperand); break;
				case bit_or:		this.arith_mul_to_bitws_ior(reach_node, statement, expression, loperand, roperand); break;
				case bit_xor:		this.arith_mul_to_bitws_xor(reach_node, statement, expression, loperand, roperand); break;
				case left_shift:	this.arith_mul_to_bitws_lsh(reach_node, statement, expression, loperand, roperand); break;
				case righ_shift:	this.arith_mul_to_bitws_rsh(reach_node, statement, expression, loperand, roperand); break;
				default: throw new IllegalArgumentException("Invalid: " + parameter);
				}
			}
			else if(operator == COperator.arith_div) {
				switch(parameter) {
				case bit_and:		this.arith_div_to_bitws_and(reach_node, statement, expression, loperand, roperand); break;
				case bit_or:		this.arith_div_to_bitws_ior(reach_node, statement, expression, loperand, roperand); break;
				case bit_xor:		this.arith_div_to_bitws_xor(reach_node, statement, expression, loperand, roperand); break;
				case left_shift:	this.arith_div_to_bitws_lsh(reach_node, statement, expression, loperand, roperand); break;
				case righ_shift:	this.arith_div_to_bitws_rsh(reach_node, statement, expression, loperand, roperand); break;
				default: throw new IllegalArgumentException("Invalid: " + parameter);
				}
			}
			else if(operator == COperator.arith_mod) {
				switch(parameter) {
				case bit_and:		this.arith_mod_to_bitws_and(reach_node, statement, expression, loperand, roperand); break;
				case bit_or:		this.arith_mod_to_bitws_ior(reach_node, statement, expression, loperand, roperand); break;
				case bit_xor:		this.arith_mod_to_bitws_xor(reach_node, statement, expression, loperand, roperand); break;
				case left_shift:	this.arith_mod_to_bitws_lsh(reach_node, statement, expression, loperand, roperand); break;
				case righ_shift:	this.arith_mod_to_bitws_rsh(reach_node, statement, expression, loperand, roperand); break;
				default: throw new IllegalArgumentException("Invalid: " + parameter);
				}
			}
			else {
				throw new IllegalArgumentException("Invalid: " + operator);
			}
		}
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
	private void arith_add_to_bitws_and(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		
		condition = this.logic_ior(this.equal_with(loperand, 0), this.equal_with(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		condition = this.logic_and(this.not_equals(loperand, 0), this.not_equals(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_and(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
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
	private void arith_add_to_bitws_ior(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		condition = this.bitws_and(expression.get_data_type(), loperand, roperand);
		constraint = SadFactory.assert_condition(statement, this.not_equals(condition, 0));
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_ior(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
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
	private void arith_add_to_bitws_xor(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		condition = this.logic_and(this.not_equals(loperand, 0), this.not_equals(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, 
					expression, this.bitws_xor(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
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
	private void arith_add_to_bitws_lsh(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition; SadAssertion constraint, state_error;
		
		lcondition = this.equal_with(loperand, 0); 
		rcondition = this.greater_eq(roperand, max_shift_size);
		constraint = SadFactory.assert_condition(statement, this.logic_ior(lcondition, rcondition));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		lcondition = this.not_equals(loperand, 0);
		rcondition = this.smaller_tn(roperand, max_shift_size);
		constraint = SadFactory.assert_condition(statement, this.logic_and(lcondition, rcondition));
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_lsh(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
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
	private void arith_add_to_bitws_rsh(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition; SadAssertion constraint, state_error;
		
		lcondition = this.equal_with(loperand, 0); 
		rcondition = this.greater_eq(roperand, max_shift_size);
		constraint = SadFactory.assert_condition(statement, this.logic_ior(lcondition, rcondition));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		lcondition = this.not_equals(loperand, 0);
		rcondition = this.smaller_tn(roperand, max_shift_size);
		constraint = SadFactory.assert_condition(statement, this.logic_and(lcondition, rcondition));
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_rsh(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
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
	private void arith_sub_to_bitws_and(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		
		condition = this.logic_ior(this.equal_with(loperand, 0), this.equal_with(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		condition = this.logic_and(this.not_equals(loperand, 0), this.not_equals(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_and(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [y != 0] --> set_expr(e, x | y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_sub_to_bitws_ior(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		constraint = SadFactory.assert_condition(statement, this.not_equals(roperand, 0));
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_ior(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x & y != y] --> set_expr(e, x ^ y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_sub_to_bitws_xor(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		condition = this.bitws_and(expression.get_data_type(), loperand, roperand);
		condition = this.not_equals(condition, roperand);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, 
					this.bitws_xor(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
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
	private void arith_sub_to_bitws_lsh(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition; SadAssertion constraint, state_error;
		
		lcondition = this.equal_with(loperand, 0); 
		rcondition = this.greater_eq(roperand, max_shift_size);
		constraint = SadFactory.assert_condition(statement, this.logic_ior(lcondition, rcondition));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		lcondition = this.not_equals(loperand, 0);
		rcondition = this.smaller_tn(roperand, max_shift_size);
		constraint = SadFactory.assert_condition(statement, this.logic_and(lcondition, rcondition));
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_lsh(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
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
	private void arith_sub_to_bitws_rsh(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression lcondition, rcondition; SadAssertion constraint, state_error;
		
		lcondition = this.equal_with(loperand, 0); 
		rcondition = this.greater_eq(roperand, max_shift_size);
		constraint = SadFactory.assert_condition(statement, this.logic_ior(lcondition, rcondition));
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		lcondition = this.not_equals(loperand, 0);
		rcondition = this.smaller_tn(roperand, max_shift_size);
		constraint = SadFactory.assert_condition(statement, this.logic_and(lcondition, rcondition));
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_rsh(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	
	/**
	 * [x != 0 && y != 0] --> set_expr(e, x & y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_mul_to_bitws_and(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		condition = this.logic_and(this.not_equals(loperand, 0), not_equals(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_and(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0 || y != 0] --> set_expr(e, x | y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_mul_to_bitws_ior(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		condition = this.logic_ior(this.not_equals(loperand, 0), not_equals(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_ior(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0 && x == y] --> set_expr(e, 0)
	 * [x != 0 || y != 0] --> set_expr(e, x ^ y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_mul_to_bitws_xor(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		
		condition = this.logic_and(this.not_equals(loperand, 0), this.equal_with(loperand, roperand));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		condition = this.logic_ior(this.not_equals(loperand, 0), this.not_equals(roperand, 0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_xor(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0 && y < size] --> set_expr(e, x << y)
	 * [x != 0 && y >=size] --> set_expr(e, 0)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_mul_to_bitws_lsh(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		
		condition = logic_and(not_equals(loperand, 0), smaller_tn(roperand, max_shift_size));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_lsh(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
		
		condition = logic_and(not_equals(loperand, 0), greater_eq(roperand, max_shift_size));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0 && y < size] --> set_expr(e, x >> y)
	 * [x != 0 && y >=size] --> set_expr(e, 0)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_mul_to_bitws_rsh(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		
		condition = logic_and(not_equals(loperand, 0), smaller_tn(roperand, max_shift_size));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_rsh(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
		
		condition = logic_and(not_equals(loperand, 0), greater_eq(roperand, max_shift_size));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
	}
	
	/**
	 * [x != 0] --> set_expr(e, x & y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_div_to_bitws_and(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		constraint = SadFactory.assert_condition(statement, this.not_equals(loperand, 0));
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_and(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 1 || y != 1] --> set_expr(e, x | y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_div_to_bitws_ior(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		constraint = SadFactory.assert_condition(statement, this.logic_ior(
				this.not_equals(loperand, 1), this.not_equals(roperand, 1)));
		state_error = SadFactory.set_expression(statement, expression, 
				this.bitws_ior(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x == y] --> set_expr(e, 0) && add_expr(e, +, -1)
	 * [x != y] --> set_expr(e, x ^ y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_div_to_bitws_xor(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		List<SadAssertion> state_errors = new ArrayList<SadAssertion>();
		
		constraint = SadFactory.assert_condition(statement, this.equal_with(loperand, roperand));
		state_errors.add(SadFactory.set_expression(statement, expression, this.sad_expression(0)));
		state_errors.add(SadFactory.add_operand(statement, expression, COperator.arith_add, this.sad_expression(-1)));
		state_error = SadFactory.conjunct(statement, state_errors);
		this.connect(reach_node, state_error, constraint);
		
		constraint = SadFactory.assert_condition(statement, this.not_equals(loperand, roperand));
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_xor(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0 && y < max_shift_size] --> set_expr(e, 0)
	 * [x != 0 && y >=max_shift_size] --> set_expr(e, x << y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_div_to_bitws_lsh(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		
		condition = logic_and(not_equals(loperand, 0), smaller_tn(roperand, max_shift_size));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		condition = logic_and(not_equals(loperand, 0), greater_eq(roperand, max_shift_size));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_lsh(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0 && y < max_shift_size] --> set_expr(e, 0)
	 * [x != 0 && y >=max_shift_size] --> set_expr(e, x >> y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_div_to_bitws_rsh(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		
		condition = logic_and(not_equals(loperand, 0), smaller_tn(roperand, max_shift_size));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		condition = logic_and(not_equals(loperand, 0), greater_eq(roperand, max_shift_size));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_rsh(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	
	/**
	 * [x != 0] --> set_expr(e, x & y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_mod_to_bitws_and(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		constraint = SadFactory.assert_condition(statement, this.not_equals(loperand, 0));
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_and(expression.get_data_type(), loperand, roperand));
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
	private void arith_mod_to_bitws_ior(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		constraint = SadFactory.assert_condition(statement, this.sad_expression(true));
		state_error = SadFactory.set_expression(statement, expression, 
				this.bitws_ior(expression.get_data_type(), loperand, roperand));
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
	private void arith_mod_to_bitws_xor(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadAssertion constraint, state_error;
		constraint = SadFactory.assert_condition(statement, this.not_equals(loperand, roperand));
		state_error = SadFactory.set_expression(statement, 
					expression, this.bitws_xor(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0 && y < max_shift_size] --> set_expr(e, 0)
	 * [x != 0 && y >=max_shift_size] --> set_expr(e, x << y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_mod_to_bitws_lsh(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		
		condition = logic_and(not_equals(loperand, 0), smaller_tn(roperand, max_shift_size));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		condition = logic_and(not_equals(loperand, 0), greater_eq(roperand, max_shift_size));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_lsh(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * [x != 0 && y < max_shift_size] --> set_expr(e, 0)
	 * [x != 0 && y >=max_shift_size] --> set_expr(e, x >> y)
	 * @param reach_node
	 * @param statement
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @throws Exception
	 */
	private void arith_mod_to_bitws_rsh(SadVertex reach_node, CirStatement statement, 
			CirExpression expression, CirExpression loperand, CirExpression roperand) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		
		condition = logic_and(not_equals(loperand, 0), smaller_tn(roperand, max_shift_size));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, this.sad_expression(0));
		this.connect(reach_node, state_error, constraint);
		
		condition = logic_and(not_equals(loperand, 0), greater_eq(roperand, max_shift_size));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, 
				expression, this.bitws_rsh(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	
}
