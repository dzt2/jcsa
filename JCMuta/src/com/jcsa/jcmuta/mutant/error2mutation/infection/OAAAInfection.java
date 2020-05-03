package com.jcsa.jcmuta.mutant.error2mutation.infection;

import java.util.List;
import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirAssignStatement;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymConstant;
import com.jcsa.jcparse.lang.symb.SymExpression;
import com.jcsa.jcparse.lang.symb.SymFactory;
import com.jcsa.jcparse.lang.symb.SymMultiExpression;

public class OAAAInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, this.get_location(mutation));
	}
	
	/* + and - */
	/**
	 * roperand != 0
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_add_to_arith_sub(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph, 
			Map<StateError, StateConstraints> output) throws Exception {
		SymExpression rvalue = this.derive_sym_constraint(SymFactory.parse(roperand));
		StateConstraints constraints = new StateConstraints(true);
		
		if(rvalue instanceof SymConstant) {
			Object constant = this.get_constant_value(((SymConstant) rvalue).get_constant());
			
			
			if(constant instanceof Boolean) {
				if(((Boolean) constant).booleanValue())
					output.put(graph.get_error_set().dif_numb(expression, -2L), constraints);
			}
			else if(constant instanceof Long) {
				long value = ((Long) constant).longValue();
				if(value != 0L) {
					output.put(graph.get_error_set().dif_numb(expression, -2 * value), constraints);
				}
			}
			else {
				double value = ((Double) constant).doubleValue();
				if(value != 0) {
					output.put(graph.get_error_set().dif_numb(expression, -2 * value), constraints);
				}
			}
		}
		else {
			SymExpression condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, rvalue, SymFactory.new_constant(0L));
			condition = this.derive_sym_constraint(condition);
			constraints.add_constraint(expression.statement_of(), condition);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}
	/**
	 * roperand != 0
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_sub_to_arith_add(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph, 
			Map<StateError, StateConstraints> output) throws Exception {
		SymExpression rvalue = this.derive_sym_constraint(SymFactory.parse(roperand));
		StateConstraints constraints = new StateConstraints(true);
		
		if(rvalue instanceof SymConstant) {
			Object constant = this.get_constant_value(((SymConstant) rvalue).get_constant());
			
			
			if(constant instanceof Boolean) {
				if(((Boolean) constant).booleanValue())
					output.put(graph.get_error_set().dif_numb(expression, 2L), constraints);
			}
			else if(constant instanceof Long) {
				long value = ((Long) constant).longValue();
				if(value != 0L) {
					output.put(graph.get_error_set().dif_numb(expression, 2 * value), constraints);
				}
			}
			else {
				double value = ((Double) constant).doubleValue();
				if(value != 0) {
					output.put(graph.get_error_set().dif_numb(expression, 2 * value), constraints);
				}
			}
		}
		else {
			SymExpression condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, rvalue, SymFactory.new_constant(0L));
			condition = this.derive_sym_constraint(condition);
			constraints.add_constraint(expression.statement_of(), condition);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}
	
	/* + and * */
	/**
	 * loperand != 0 or roperand != 0
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_add_to_arith_mul(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph, 
			Map<StateError, StateConstraints> output) throws Exception {
		StateConstraints constraints = new StateConstraints(false);
		
		SymExpression lcondition = SymFactory.parse(loperand);
		lcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, lcondition, SymFactory.new_constant(0L));
		lcondition = this.derive_sym_constraint(lcondition);
		
		SymExpression rcondition = SymFactory.parse(roperand);
		rcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, rcondition, SymFactory.new_constant(0L));
		rcondition = this.derive_sym_constraint(rcondition);
		
		constraints.add_constraint(expression.statement_of(), lcondition);
		constraints.add_constraint(expression.statement_of(), rcondition);
		output.put(graph.get_error_set().chg_numb(expression), constraints);
	}
	/**
	 * loperand != 0 or roperand != 0
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_mul_to_arith_add(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph, 
			Map<StateError, StateConstraints> output) throws Exception {
		StateConstraints constraints = new StateConstraints(false);
		
		SymExpression lcondition = SymFactory.parse(loperand);
		lcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, lcondition, SymFactory.new_constant(0L));
		lcondition = this.derive_sym_constraint(lcondition);
		
		SymExpression rcondition = SymFactory.parse(roperand);
		rcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, rcondition, SymFactory.new_constant(0L));
		rcondition = this.derive_sym_constraint(rcondition);
		
		constraints.add_constraint(expression.statement_of(), lcondition);
		constraints.add_constraint(expression.statement_of(), rcondition);
		output.put(graph.get_error_set().chg_numb(expression), constraints);
	}
	
	/* + and / */
	/**
	 * roperand = 0 --> failure
	 * roperand !=0 --> chg_numb
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_add_to_arith_div(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph, 
			Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lcondition = SymFactory.parse(roperand);
		lcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.equal_with, lcondition, SymFactory.new_constant(0L));
		lcondition = this.derive_sym_constraint(lcondition);
		StateConstraints constraint1 = new StateConstraints(true);
		constraint1.add_constraint(expression.statement_of(), lcondition);
		
		SymExpression rcondition = SymFactory.parse(roperand);
		rcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, rcondition, SymFactory.new_constant(0L));
		rcondition = this.derive_sym_constraint(rcondition);
		StateConstraints constraint2 = new StateConstraints(true);
		constraint2.add_constraint(expression.statement_of(), rcondition);
		
		output.put(graph.get_error_set().failure(), constraint1);
		output.put(graph.get_error_set().chg_numb(expression), constraint2);
	}
	/**
	 * --> chg_numb
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_div_to_arith_add(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph, 
			Map<StateError, StateConstraints> output) throws Exception {
		output.put(graph.get_error_set().chg_numb(expression), new StateConstraints(true));
	}
	
	/* + and % */
	/**
	 * roperand = 0 --> failure
	 * roperand = 1 --> set_numb
	 * x < -2 * y 	--> chg_numb
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_add_to_arith_mod(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph, 
			Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lcondition = SymFactory.parse(roperand);
		lcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.equal_with, lcondition, SymFactory.new_constant(0L));
		lcondition = this.derive_sym_constraint(lcondition);
		StateConstraints constraint1 = new StateConstraints(true);
		constraint1.add_constraint(expression.statement_of(), lcondition);
		
		SymExpression rcondition = SymFactory.parse(roperand);
		rcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, rcondition, SymFactory.new_constant(0L));
		rcondition = this.derive_sym_constraint(rcondition);
		StateConstraints constraint2 = new StateConstraints(true);
		constraint2.add_constraint(expression.statement_of(), rcondition);
		
		SymExpression x = SymFactory.parse(loperand);
		SymExpression y = SymFactory.parse(roperand);
		SymMultiExpression y2 = SymFactory.new_multiple_expression(
						roperand.get_data_type(), COperator.arith_mul);
		y2.add_operand(y); y2.add_operand(SymFactory.new_constant(-2L));
		SymExpression mcondition = SymFactory.new_binary_expression(
				CBasicTypeImpl.bool_type, COperator.smaller_tn, x, y2);
		mcondition = this.derive_sym_constraint(mcondition);
		StateConstraints constraint3 = new StateConstraints(true);
		constraint3.add_constraint(expression.statement_of(), mcondition);
		
		output.put(graph.get_error_set().failure(), constraint1);
		output.put(graph.get_error_set().chg_numb(expression), constraint2);
		output.put(graph.get_error_set().set_numb(expression, 0L), constraint3);
	}
	/**
	 * x < -2 * y 	--> chg_numb
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_mod_to_arith_add(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph, 
			Map<StateError, StateConstraints> output) throws Exception {
		SymExpression x = SymFactory.parse(loperand);
		SymExpression y = SymFactory.parse(roperand);
		SymMultiExpression y2 = SymFactory.new_multiple_expression(
						roperand.get_data_type(), COperator.arith_mul);
		y2.add_operand(y); y2.add_operand(SymFactory.new_constant(-2L));
		SymExpression mcondition = SymFactory.new_binary_expression(
				CBasicTypeImpl.bool_type, COperator.smaller_tn, x, y2);
		mcondition = this.derive_sym_constraint(mcondition);
		StateConstraints constraint3 = new StateConstraints(true);
		constraint3.add_constraint(expression.statement_of(), mcondition);
		
		output.put(graph.get_error_set().chg_numb(expression), constraint3);
	}
	
	/* - and * */
	/**
	 * loperand != 0 or roperand != 0
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_sub_to_arith_mul(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph, 
			Map<StateError, StateConstraints> output) throws Exception {
		StateConstraints constraints = new StateConstraints(false);
		
		SymExpression lcondition = SymFactory.parse(loperand);
		lcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, lcondition, SymFactory.new_constant(0L));
		lcondition = this.derive_sym_constraint(lcondition);
		
		SymExpression rcondition = SymFactory.parse(roperand);
		rcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, rcondition, SymFactory.new_constant(0L));
		rcondition = this.derive_sym_constraint(rcondition);
		
		constraints.add_constraint(expression.statement_of(), lcondition);
		constraints.add_constraint(expression.statement_of(), rcondition);
		output.put(graph.get_error_set().chg_numb(expression), constraints);
	}
	/**
	 * loperand != 0 or roperand != 0
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_mul_to_arith_sub(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph, 
			Map<StateError, StateConstraints> output) throws Exception {
		StateConstraints constraints = new StateConstraints(false);
		
		SymExpression lcondition = SymFactory.parse(loperand);
		lcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, lcondition, SymFactory.new_constant(0L));
		lcondition = this.derive_sym_constraint(lcondition);
		
		SymExpression rcondition = SymFactory.parse(roperand);
		rcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, rcondition, SymFactory.new_constant(0L));
		rcondition = this.derive_sym_constraint(rcondition);
		
		constraints.add_constraint(expression.statement_of(), lcondition);
		constraints.add_constraint(expression.statement_of(), rcondition);
		output.put(graph.get_error_set().chg_numb(expression), constraints);
	}
	
	/* - and / */
	/**
	 * roperand = 0 --> failure
	 * roperand !=0 --> chg_numb
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_sub_to_arith_div(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph, 
			Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lcondition = SymFactory.parse(roperand);
		lcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.equal_with, lcondition, SymFactory.new_constant(0L));
		lcondition = this.derive_sym_constraint(lcondition);
		StateConstraints constraint1 = new StateConstraints(true);
		constraint1.add_constraint(expression.statement_of(), lcondition);
		
		SymExpression rcondition = SymFactory.parse(roperand);
		rcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, rcondition, SymFactory.new_constant(0L));
		rcondition = this.derive_sym_constraint(rcondition);
		StateConstraints constraint2 = new StateConstraints(true);
		constraint2.add_constraint(expression.statement_of(), rcondition);
		
		output.put(graph.get_error_set().failure(), constraint1);
		output.put(graph.get_error_set().chg_numb(expression), constraint2);
	}
	/**
	 * --> chg_numb
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_div_to_arith_sub(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph, 
			Map<StateError, StateConstraints> output) throws Exception {
		output.put(graph.get_error_set().chg_numb(expression), new StateConstraints(true));
	}
	
	/* - and % */
	/**
	 * roperand = 0 --> failure
	 * roperand = 1 --> set_numb
	 * x > 2 * y    --> chg_numb
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_sub_to_arith_mod(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph, 
			Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lcondition = SymFactory.parse(roperand);
		lcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.equal_with, lcondition, SymFactory.new_constant(0L));
		lcondition = this.derive_sym_constraint(lcondition);
		StateConstraints constraint1 = new StateConstraints(true);
		constraint1.add_constraint(expression.statement_of(), lcondition);
		
		SymExpression rcondition = SymFactory.parse(roperand);
		rcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, rcondition, SymFactory.new_constant(0L));
		rcondition = this.derive_sym_constraint(rcondition);
		StateConstraints constraint2 = new StateConstraints(true);
		constraint2.add_constraint(expression.statement_of(), rcondition);
		
		SymExpression x = SymFactory.parse(loperand);
		SymExpression y = SymFactory.parse(roperand);
		SymMultiExpression y2 = SymFactory.new_multiple_expression(
						roperand.get_data_type(), COperator.arith_mul);
		y2.add_operand(y); y2.add_operand(SymFactory.new_constant(2L));
		SymExpression mcondition = SymFactory.new_binary_expression(
				CBasicTypeImpl.bool_type, COperator.greater_tn, x, y2);
		mcondition = this.derive_sym_constraint(mcondition);
		StateConstraints constraint3 = new StateConstraints(true);
		constraint3.add_constraint(expression.statement_of(), mcondition);
		
		output.put(graph.get_error_set().failure(), constraint1);
		output.put(graph.get_error_set().chg_numb(expression), constraint2);
		output.put(graph.get_error_set().set_numb(expression, 0L), constraint3);
	}
	/**
	 * x > 2 * y    --> chg_numb
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_mod_to_arith_sub(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph, 
			Map<StateError, StateConstraints> output) throws Exception {
		SymExpression x = SymFactory.parse(loperand);
		SymExpression y = SymFactory.parse(roperand);
		SymMultiExpression y2 = SymFactory.new_multiple_expression(
						roperand.get_data_type(), COperator.arith_mul);
		y2.add_operand(y); y2.add_operand(SymFactory.new_constant(2L));
		SymExpression mcondition = SymFactory.new_binary_expression(
				CBasicTypeImpl.bool_type, COperator.greater_tn, x, y2);
		mcondition = this.derive_sym_constraint(mcondition);
		StateConstraints constraint3 = new StateConstraints(true);
		constraint3.add_constraint(expression.statement_of(), mcondition);
		
		output.put(graph.get_error_set().chg_numb(expression), constraint3);
	}
	
	/* * and / */
	/**
	 * roperand = 0 --> failure
	 * loperand != 0 and roperand != 1 and roperand != -1
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_mul_to_arith_div(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph, 
			Map<StateError, StateConstraints> output) throws Exception {
		/* roperand = 0 --> failure */
		StateConstraints constraint1 = new StateConstraints(true);
		SymExpression condition1 = SymFactory.parse(roperand);
		condition1 = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.equal_with, condition1, SymFactory.new_constant(0L));
		condition1 = this.derive_sym_constraint(condition1);
		constraint1.add_constraint(expression.statement_of(), condition1);
		output.put(graph.get_error_set().failure(), constraint1);
		
		/* loperand != 0, roperand != 1 and roperand != -1 */
		SymExpression lcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, SymFactory.parse(loperand), SymFactory.new_constant(0L));
		lcondition = this.derive_sym_constraint(lcondition);
		SymExpression mcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, SymFactory.parse(roperand), SymFactory.new_constant(1L));
		mcondition = this.derive_sym_constraint(mcondition);
		SymExpression rcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, SymFactory.parse(roperand), SymFactory.new_constant(-1L));
		rcondition = this.derive_sym_constraint(rcondition);
		StateConstraints constraint2 = new StateConstraints(true);
		constraint2.add_constraint(expression.statement_of(), lcondition);
		constraint2.add_constraint(expression.statement_of(), mcondition);
		constraint2.add_constraint(expression.statement_of(), rcondition);
		output.put(graph.get_error_set().chg_numb(expression), constraint2);
	}
	/**
	 * loperand != 0 and roperand != 1 and roperand != -1
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_div_to_arith_mul(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph, 
			Map<StateError, StateConstraints> output) throws Exception {
		/* loperand != 0, roperand != 1 and roperand != -1 */
		SymExpression lcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, SymFactory.parse(loperand), SymFactory.new_constant(0L));
		lcondition = this.derive_sym_constraint(lcondition);
		SymExpression mcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, SymFactory.parse(roperand), SymFactory.new_constant(1L));
		mcondition = this.derive_sym_constraint(mcondition);
		SymExpression rcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, SymFactory.parse(roperand), SymFactory.new_constant(-1L));
		rcondition = this.derive_sym_constraint(rcondition);
		StateConstraints constraint = new StateConstraints(true);
		constraint.add_constraint(expression.statement_of(), lcondition);
		constraint.add_constraint(expression.statement_of(), mcondition);
		constraint.add_constraint(expression.statement_of(), rcondition);
		output.put(graph.get_error_set().chg_numb(expression), constraint);
	}
	
	/* * and % */
	/**
	 * roperand = 0 --> failure
	 * loperand != 0 --> chg_numb
	 * loperand = k * roperand --> set_numb
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_mul_to_arith_mod(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph, 
			Map<StateError, StateConstraints> output) throws Exception {
		/* roperand = 0 --> failure */
		StateConstraints constraint1 = new StateConstraints(true);
		SymExpression condition1 = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.equal_with, SymFactory.parse(roperand), SymFactory.new_constant(0L));
		condition1 = this.derive_sym_constraint(condition1);
		constraint1.add_constraint(expression.statement_of(), condition1);
		output.put(graph.get_error_set().failure(), constraint1);
		
		/* loperand != 0 --> chg_numb */
		StateConstraints constraint2 = new StateConstraints(true);
		SymExpression condition2 = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, SymFactory.parse(loperand), SymFactory.new_constant(0L));
		condition2 = this.derive_sym_constraint(condition2);
		constraint2.add_constraint(expression.statement_of(), condition2);
		output.put(graph.get_error_set().chg_numb(expression), constraint2);
		
		/* loperand = k * roperand --> set_numb */
		StateConstraints constraint3 = new StateConstraints(true);
		SymMultiExpression rvalue = SymFactory.new_multiple_expression(roperand.get_data_type(), COperator.arith_mul);
		rvalue.add_operand(SymFactory.new_address(StateInfection.AnyInteger, CBasicTypeImpl.long_type));
		rvalue.add_operand(SymFactory.parse(roperand));
		SymExpression condition3 = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.equal_with, SymFactory.parse(loperand), rvalue);
		condition3 = this.derive_sym_constraint(condition3);
		constraint3.add_constraint(expression.statement_of(), condition3);
		output.put(graph.get_error_set().set_numb(expression, 0L), constraint3);
	}
	/**
	 * loperand != 0 --> chg_numb
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_mod_to_arith_mul(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph, 
			Map<StateError, StateConstraints> output) throws Exception {
		/* loperand != 0 --> chg_numb */
		StateConstraints constraint2 = new StateConstraints(true);
		SymExpression condition2 = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, SymFactory.parse(loperand), SymFactory.new_constant(0L));
		condition2 = this.derive_sym_constraint(condition2);
		constraint2.add_constraint(expression.statement_of(), condition2);
		output.put(graph.get_error_set().chg_numb(expression), constraint2);
	}
	
	/*/ and % */
	/**
	 * loperand != 0 --> chg_numb
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_div_to_arith_mod(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph, 
			Map<StateError, StateConstraints> output) throws Exception {
		StateConstraints constraint = new StateConstraints(true);
		SymExpression condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, SymFactory.parse(loperand), SymFactory.new_constant(0L));
		condition = this.derive_sym_constraint(condition);
		constraint.add_constraint(expression.statement_of(), condition);
		output.put(graph.get_error_set().chg_numb(expression), constraint);
	}
	/**
	 * loperand != 0 --> chg_numb
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_mod_to_arith_div(CirExpression expression, 
			CirExpression loperand, CirExpression roperand, StateErrorGraph graph, 
			Map<StateError, StateConstraints> output) throws Exception {
		StateConstraints constraint = new StateConstraints(true);
		SymExpression condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
				COperator.not_equals, SymFactory.parse(loperand), SymFactory.new_constant(0L));
		condition = this.derive_sym_constraint(condition);
		constraint.add_constraint(expression.statement_of(), condition);
		output.put(graph.get_error_set().chg_numb(expression), constraint);
	}
	
	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/* getters */
		List<CirNode> statements = cir_tree.get_cir_nodes(
				this.get_location(mutation), CirAssignStatement.class);
		CirAssignStatement statement = (CirAssignStatement) statements.get(statements.size() - 1);
		CirComputeExpression expression = (CirComputeExpression) statement.get_rvalue();
		CirExpression loperand = expression.get_operand(0), roperand = expression.get_operand(1);
		
		switch(mutation.get_mutation_operator()) {
		case arith_add_assign_to_arith_sub_assign: this.arith_add_to_arith_sub(expression, loperand, roperand, graph, output); break;
		case arith_add_assign_to_arith_mul_assign: this.arith_add_to_arith_mul(expression, loperand, roperand, graph, output); break;
		case arith_add_assign_to_arith_div_assign: this.arith_add_to_arith_div(expression, loperand, roperand, graph, output); break;
		case arith_add_assign_to_arith_mod_assign: this.arith_add_to_arith_mod(expression, loperand, roperand, graph, output); break;
		
		case arith_sub_assign_to_arith_add_assign: this.arith_sub_to_arith_add(expression, loperand, roperand, graph, output); break;
		case arith_sub_assign_to_arith_mul_assign: this.arith_sub_to_arith_mul(expression, loperand, roperand, graph, output); break;
		case arith_sub_assign_to_arith_div_assign: this.arith_sub_to_arith_div(expression, loperand, roperand, graph, output); break;
		case arith_sub_assign_to_arith_mod_assign: this.arith_sub_to_arith_mod(expression, loperand, roperand, graph, output); break;
		
		case arith_mul_assign_to_arith_add_assign: this.arith_mul_to_arith_add(expression, loperand, roperand, graph, output); break;
		case arith_mul_assign_to_arith_sub_assign: this.arith_mul_to_arith_sub(expression, loperand, roperand, graph, output); break;
		case arith_mul_assign_to_arith_div_assign: this.arith_mul_to_arith_div(expression, loperand, roperand, graph, output); break;
		case arith_mul_assign_to_arith_mod_assign: this.arith_mul_to_arith_mod(expression, loperand, roperand, graph, output); break;
		
		case arith_div_assign_to_arith_add_assign: this.arith_div_to_arith_add(expression, loperand, roperand, graph, output); break;
		case arith_div_assign_to_arith_sub_assign: this.arith_div_to_arith_sub(expression, loperand, roperand, graph, output); break;
		case arith_div_assign_to_arith_mul_assign: this.arith_div_to_arith_mul(expression, loperand, roperand, graph, output); break;
		case arith_div_assign_to_arith_mod_assign: this.arith_div_to_arith_mod(expression, loperand, roperand, graph, output); break;
		
		case arith_mod_assign_to_arith_add_assign: this.arith_mod_to_arith_add(expression, loperand, roperand, graph, output); break;
		case arith_mod_assign_to_arith_sub_assign: this.arith_mod_to_arith_sub(expression, loperand, roperand, graph, output); break;
		case arith_mod_assign_to_arith_div_assign: this.arith_mod_to_arith_div(expression, loperand, roperand, graph, output); break;
		case arith_mod_assign_to_arith_mul_assign: this.arith_mod_to_arith_mul(expression, loperand, roperand, graph, output); break;
		
		default: throw new IllegalArgumentException(mutation.get_mutation_operator().toString());
		}
	}

}
