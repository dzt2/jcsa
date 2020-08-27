package com.jcsa.jcmutest.mutant.sad2mutant.muta.infect;

import java.util.ArrayList;
import java.util.List;

import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadAssertion;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadExpression;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadFactory;
import com.jcsa.jcmutest.mutant.sad2mutant.lang.SadParser;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadInfection;
import com.jcsa.jcmutest.mutant.sad2mutant.muta.SadVertex;
import com.jcsa.jcparse.lang.astree.expr.oprt.AstBinaryExpression;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;

public class OAANSadInfection extends SadInfection {
	
	/* x + y */
	private void arith_add_to_arith_add(SadVertex reach_node, CirExpression expression,
			CirExpression loperand, CirExpression roperand, CirStatement statement) throws Exception {}
	/**
	 * 	{roperand != 0}
	 * 	==> 
	 * 	set_expr(expr, x - y)
	 * 	and
	 * 	add_operand(expr, +, -2 * y)
	 * @param reach_node
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param statement
	 * @throws Exception
	 */
	private void arith_add_to_arith_sub(SadVertex reach_node, CirExpression expression,
			CirExpression loperand, CirExpression roperand, CirStatement statement) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		List<SadAssertion> state_errors = new ArrayList<SadAssertion>();
		List<SadExpression> operands = new ArrayList<SadExpression>();
		
		condition = SadFactory.not_equals(CBasicTypeImpl.bool_type, 
				(SadExpression) SadParser.cir_parse(loperand), 
				SadFactory.constant(0));
		constraint = SadFactory.assert_condition(statement, condition);
		
		state_errors.add(SadFactory.set_expression(statement, expression, 
				SadFactory.arith_sub(expression.get_data_type(), loperand, roperand)));
		operands.add(SadFactory.constant(-2));
		operands.add((SadExpression) SadParser.cir_parse(roperand));
		state_errors.add(SadFactory.add_operand(statement, expression, COperator.
				arith_add, SadFactory.arith_mul(expression.get_data_type(), operands)));
		state_error = SadFactory.conjunct(statement, state_errors);
		
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * {x == 0 || y == 0} --> set_expr(expr, 0)
	 * {x != 0 && y != 0} --> set_expr(expr, x * y)
	 * 
	 * @param reach_node
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param statement
	 * @throws Exception
	 */
	private void arith_add_to_arith_mul(SadVertex reach_node, CirExpression expression,
			CirExpression loperand, CirExpression roperand, CirStatement statement) throws Exception {
		SadExpression lcondition, rcondition, condition; SadAssertion constraint, state_error;
		List<SadExpression> operands = new ArrayList<SadExpression>();
		
		lcondition = SadFactory.equal_with(CBasicTypeImpl.bool_type, 
				(SadExpression) SadParser.cir_parse(loperand), SadFactory.constant(0));
		rcondition = SadFactory.equal_with(CBasicTypeImpl.bool_type, 
				(SadExpression) SadParser.cir_parse(roperand), SadFactory.constant(0));
		operands.clear(); operands.add(lcondition); operands.add(rcondition);
		condition = SadFactory.logic_ior(CBasicTypeImpl.bool_type, operands);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, SadFactory.constant(0));
		this.connect(reach_node, state_error, constraint);
		
		lcondition = SadFactory.not_equals(CBasicTypeImpl.bool_type, 
				(SadExpression) SadParser.cir_parse(loperand), SadFactory.constant(0));
		rcondition = SadFactory.not_equals(CBasicTypeImpl.bool_type, 
				(SadExpression) SadParser.cir_parse(roperand), SadFactory.constant(0));
		operands.clear(); operands.add(lcondition); operands.add(rcondition);
		condition = SadFactory.logic_and(CBasicTypeImpl.bool_type, operands);
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, 
				SadFactory.arith_mul(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	/**
	 * {roperand == 0} --> trapping()
	 * {loperand == 0} --> set_expr(expression, 0)
	 * {roperand != 0} --> set_expr(expression, x / y)
	 * @param reach_node
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param statement
	 * @throws Exception
	 */
	private void arith_add_to_arith_div(SadVertex reach_node, CirExpression expression,
			CirExpression loperand, CirExpression roperand, CirStatement statement) throws Exception {
		SadExpression condition; SadAssertion constraint, state_error;
		
		condition = SadFactory.equal_with(CBasicTypeImpl.bool_type, 
				(SadExpression) SadParser.cir_parse(roperand), 
				SadFactory.constant(0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.trap_statement(statement);
		this.connect(reach_node, state_error, constraint);
		
		condition = SadFactory.equal_with(CBasicTypeImpl.bool_type, 
				(SadExpression) SadParser.cir_parse(loperand), 
				SadFactory.constant(0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, SadFactory.constant(0));
		this.connect(reach_node, state_error, constraint);
		
		condition = SadFactory.not_equals(CBasicTypeImpl.bool_type, 
				(SadExpression) SadParser.cir_parse(roperand), 
				SadFactory.constant(0));
		constraint = SadFactory.assert_condition(statement, condition);
		state_error = SadFactory.set_expression(statement, expression, SadFactory.
						arith_div(expression.get_data_type(), loperand, roperand));
		this.connect(reach_node, state_error, constraint);
	}
	
	
	@Override
	protected void get_infect(CirTree tree, AstMutation mutation, SadVertex reach_node) throws Exception {
		AstBinaryExpression location = (AstBinaryExpression) mutation.get_location();
		CirComputeExpression expression = (CirComputeExpression) find_result(tree, location);
		CirStatement statement = expression.statement_of();
		CirExpression loperand = expression.get_operand(0), roperand = expression.get_operand(1);
		COperator op1 = location.get_operator().get_operator();
		COperator op2 = (COperator) mutation.get_parameter();
		
	}

}
