package com.jcsa.jcmuta.mutant.error2mutation.infection;

import java.util.Map;

import com.jcsa.jcmuta.mutant.AstMutation;
import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcparse.lang.ctype.impl.CBasicTypeImpl;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymConstant;
import com.jcsa.jcparse.lang.symb.SymExpression;
import com.jcsa.jcparse.lang.symb.SymFactory;
import com.jcsa.jcparse.lang.symb.SymMultiExpression;

public class OAANInfection extends StateInfection {

	@Override
	protected CirStatement get_location(CirTree cir_tree, AstMutation mutation) throws Exception {
		return this.get_beg_statement(cir_tree, this.get_location(mutation));
	}
	
	@Override
	protected void get_infections(CirTree cir_tree, AstMutation mutation, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) 
				this.get_result_of(cir_tree, this.get_location(mutation));
		CirExpression loperand = expression.get_operand(0);
		CirExpression roperand = expression.get_operand(1);
		
		switch(mutation.get_mutation_operator()) {
		case arith_add_to_arith_sub:	this.arith_add_to_arith_sub(expression, loperand, roperand, graph, output); break;
		case arith_add_to_arith_mul:	this.arith_add_to_arith_mul(expression, loperand, roperand, graph, output); break;
		case arith_add_to_arith_div:	this.arith_add_to_arith_div(expression, loperand, roperand, graph, output); break;
		case arith_add_to_arith_mod:	this.arith_add_to_arith_mod(expression, loperand, roperand, graph, output); break;
		
		case arith_sub_to_arith_add:	this.arith_sub_to_arith_add(expression, loperand, roperand, graph, output); break;
		case arith_sub_to_arith_mul:	this.arith_sub_to_arith_mul(expression, loperand, roperand, graph, output); break;
		case arith_sub_to_arith_div:	this.arith_sub_to_arith_div(expression, loperand, roperand, graph, output); break;
		case arith_sub_to_arith_mod:	this.arith_sub_to_arith_mod(expression, loperand, roperand, graph, output); break;
		
		case arith_mul_to_arith_add:	this.arith_mul_to_arith_add(expression, loperand, roperand, graph, output); break;
		case arith_mul_to_arith_sub:	this.arith_mul_to_arith_sub(expression, loperand, roperand, graph, output); break;
		case arith_mul_to_arith_div:	this.arith_mul_to_arith_div(expression, loperand, roperand, graph, output); break;
		case arith_mul_to_arith_mod:	this.arith_mul_to_arith_mod(expression, loperand, roperand, graph, output); break;
		
		case arith_div_to_arith_add:	this.arith_div_to_arith_add(expression, loperand, roperand, graph, output); break;
		case arith_div_to_arith_sub:	this.arith_div_to_arith_sub(expression, loperand, roperand, graph, output); break;
		case arith_div_to_arith_mul:	this.arith_div_to_arith_mul(expression, loperand, roperand, graph, output); break;
		case arith_div_to_arith_mod:	this.arith_div_to_arith_mod(expression, loperand, roperand, graph, output); break;
		
		case arith_mod_to_arith_add:	this.arith_mod_to_arith_add(expression, loperand, roperand, graph, output); break;
		case arith_mod_to_arith_sub:	this.arith_mod_to_arith_sub(expression, loperand, roperand, graph, output); break;
		case arith_mod_to_arith_mul:	this.arith_mod_to_arith_mul(expression, loperand, roperand, graph, output); break;
		case arith_mod_to_arith_div:	this.arith_mod_to_arith_div(expression, loperand, roperand, graph, output); break;
		default: throw new IllegalArgumentException(mutation.get_mutation_operator().toString());
		}
	}
	
	/* implementation methods */
	/* (+, -) */
	/**
	 * roperand != 0 --> chg_numb
	 * roperand is const --> dif_numb
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_add_to_arith_sub(CirExpression expression, CirExpression loperand, CirExpression 
			roperand, StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression rvalue = this.derive_sym_constraint(SymFactory.parse(roperand));
		StateConstraints constraints = new StateConstraints(true);
		
		if(rvalue instanceof SymConstant) {
			Object constant = this.get_constant_value(((SymConstant) rvalue).get_constant());
			if(constant instanceof Boolean) {
				if(((Boolean) constant).booleanValue()) {
					output.put(graph.get_error_set().dif_numb(expression, -2L), constraints);
				}
			}
			else if(constant instanceof Long) {
				long value = ((Long) constant).longValue();
				if(value != 0) {
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
					COperator.not_equals, SymFactory.parse(roperand), SymFactory.new_constant(0L));
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition));
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}
	/**
	 * roperand != 0 --> chg_numb
	 * roperand is const --> dif_numb
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_sub_to_arith_add(CirExpression expression, CirExpression loperand, CirExpression 
			roperand, StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression rvalue = this.derive_sym_constraint(SymFactory.parse(roperand));
		StateConstraints constraints = new StateConstraints(true);
		
		if(rvalue instanceof SymConstant) {
			Object constant = this.get_constant_value(((SymConstant) rvalue).get_constant());
			if(constant instanceof Boolean) {
				if(((Boolean) constant).booleanValue()) {
					output.put(graph.get_error_set().dif_numb(expression, 2L), constraints);
				}
			}
			else if(constant instanceof Long) {
				long value = ((Long) constant).longValue();
				if(value != 0) {
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
					COperator.not_equals, SymFactory.parse(roperand), SymFactory.new_constant(0L));
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition));
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}
	
	/* (+, *) */
	/**
	 * x != y / (y - 1)
	 * x = 0, y = 0
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_add_to_arith_mul(CirExpression expression, CirExpression loperand, CirExpression 
			roperand, StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lop = this.derive_sym_constraint(SymFactory.parse(loperand));
		SymExpression rop = this.derive_sym_constraint(SymFactory.parse(roperand));
		
		if(lop instanceof SymConstant && rop instanceof SymConstant) {
			StateConstraints constraints = new StateConstraints(true);
			Object lvalue = this.get_constant_value(((SymConstant) lop).get_constant());
			Object rvalue = this.get_constant_value(((SymConstant) rop).get_constant());
			if(lvalue instanceof Long) {
				long lconstant = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(rconstant * lconstant != rconstant + lconstant) {
						output.put(graph.get_error_set().set_numb(expression, rconstant * lconstant), constraints);
					}
				}
				else {
					double rconstant = ((Double) rvalue).doubleValue();
					if(rconstant * lconstant != rconstant + lconstant) {
						output.put(graph.get_error_set().set_numb(expression, rconstant * lconstant), constraints);
					}
				}
			}
			else {
				double lconstant = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(rconstant * lconstant != rconstant + lconstant) {
						output.put(graph.get_error_set().set_numb(expression, rconstant * lconstant), constraints);
					}
				}
				else {
					double rconstant = ((Double) rvalue).doubleValue();
					if(rconstant * lconstant != rconstant + lconstant) {
						output.put(graph.get_error_set().set_numb(expression, rconstant * lconstant), constraints);
					}
				}
			}
		}
		else {
			SymExpression lcondition, rcondition; StateConstraints constraints;
			
			/* x != 0 and y != 0 --> chg_numb */
			lcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, SymFactory.parse(loperand), SymFactory.new_constant(0L));
			rcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, SymFactory.parse(roperand), SymFactory.new_constant(0L));
			constraints = new StateConstraints(true);
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(lcondition));
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(rcondition));
			output.put(graph.get_error_set().chg_numb(expression), constraints);
			
			/* x == 0 or y == 0 --> set_numb */
			lcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.equal_with, SymFactory.parse(loperand), SymFactory.new_constant(0L));
			rcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.equal_with, SymFactory.parse(roperand), SymFactory.new_constant(0L));
			constraints = new StateConstraints(false);
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(lcondition));
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(rcondition));
			output.put(graph.get_error_set().set_numb(expression, 0L), constraints);
		}
	}
	/**
	 * x != y / (y - 1)
	 * {0, 0}
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_mul_to_arith_add(CirExpression expression, CirExpression loperand, CirExpression 
			roperand, StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lop = this.derive_sym_constraint(SymFactory.parse(loperand));
		SymExpression rop = this.derive_sym_constraint(SymFactory.parse(roperand));
		StateConstraints constraints = new StateConstraints(true);
		
		if(lop instanceof SymConstant && rop instanceof SymConstant) {
			Object lvalue = this.get_constant_value(((SymConstant) lop).get_constant());
			Object rvalue = this.get_constant_value(((SymConstant) rop).get_constant());
			if(lvalue instanceof Long) {
				long lconstant = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(rconstant * lconstant != rconstant + lconstant) {
						output.put(graph.get_error_set().set_numb(expression, rconstant + lconstant), constraints);
					}
				}
				else {
					double rconstant = ((Double) rvalue).doubleValue();
					if(rconstant * lconstant != rconstant + lconstant) {
						output.put(graph.get_error_set().set_numb(expression, rconstant + lconstant), constraints);
					}
				}
			}
			else {
				double lconstant = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(rconstant * lconstant != rconstant + lconstant) {
						output.put(graph.get_error_set().set_numb(expression, rconstant + lconstant), constraints);
					}
				}
				else {
					double rconstant = ((Double) rvalue).doubleValue();
					if(rconstant * lconstant != rconstant + lconstant) {
						output.put(graph.get_error_set().set_numb(expression, rconstant + lconstant), constraints);
					}
				}
			}
		}
		else {
			/* (x != 0 and y != 0) --> chg_numb */
			SymExpression condition1 = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, SymFactory.parse(loperand), SymFactory.new_constant(0L));
			SymExpression condition2 = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, SymFactory.parse(roperand), SymFactory.new_constant(0L));
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition1));
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition2));
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}
	
	/* (+, /) */
	/**
	 * roperand = 0 --> failure
	 * roperand !=0 --> chg_numb
	 * loperand = 0 --> set_numb
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_add_to_arith_div(CirExpression expression, CirExpression loperand, CirExpression 
			roperand, StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lop = this.derive_sym_constraint(SymFactory.parse(loperand));
		SymExpression rop = this.derive_sym_constraint(SymFactory.parse(roperand));
		
		if(lop instanceof SymConstant && rop instanceof SymConstant) {
			StateConstraints constraints = new StateConstraints(true);
			Object lvalue = this.get_constant_value(((SymConstant) lop).get_constant());
			Object rvalue = this.get_constant_value(((SymConstant) rop).get_constant());
			if(lvalue instanceof Long) {
				long lconstant = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(rconstant == 0)
						output.put(graph.get_error_set().failure(), constraints);
					else if(lconstant + rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant / rconstant), constraints);
				}
				else {
					double rconstant = ((Double) rvalue).doubleValue();
					if(rconstant == 0)
						output.put(graph.get_error_set().failure(), constraints);
					else if(lconstant + rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant / rconstant), constraints);
				}
			}
			else {
				double lconstant = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(rconstant == 0)
						output.put(graph.get_error_set().failure(), constraints);
					else if(lconstant + rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant / rconstant), constraints);
				}
				else {
					double rconstant = ((Double) rvalue).doubleValue();
					if(rconstant == 0)
						output.put(graph.get_error_set().failure(), constraints);
					else if(lconstant + rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant / rconstant), constraints);
				}
			}
		}
		else {
			SymExpression condition1 = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.equal_with, SymFactory.parse(roperand), SymFactory.new_constant(0L));
			SymExpression condition2 = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, SymFactory.parse(roperand), SymFactory.new_constant(0L));
			SymExpression condition3 = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.equal_with, SymFactory.parse(loperand), SymFactory.new_constant(0L));
			
			/* (y = 0) --> trapping */
			StateConstraints constraint1 = new StateConstraints(true);
			constraint1.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition1));
			output.put(graph.get_error_set().failure(), constraint1);
			
			/* (y != 0) --> chg_numb */
			StateConstraints constraint2 = new StateConstraints(true);
			constraint2.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition2));
			output.put(graph.get_error_set().chg_numb(expression), constraint2);
			
			/* (x = 0) --> set_numb */
			StateConstraints constraint3 = new StateConstraints(true);
			constraint3.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition3));
			output.put(graph.get_error_set().set_numb(expression, 0L), constraint3);
		}
	}
	/**
	 * chg_numb
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_div_to_arith_add(CirExpression expression, CirExpression loperand, CirExpression 
			roperand, StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lop = this.derive_sym_constraint(SymFactory.parse(loperand));
		SymExpression rop = this.derive_sym_constraint(SymFactory.parse(roperand));
		
		if(lop instanceof SymConstant && rop instanceof SymConstant) {
			StateConstraints constraints = new StateConstraints(true);
			Object lvalue = this.get_constant_value(((SymConstant) lop).get_constant());
			Object rvalue = this.get_constant_value(((SymConstant) rop).get_constant());
			if(lvalue instanceof Long) {
				long lconstant = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(lconstant + rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant + rconstant), constraints);
				}
				else {
					double rconstant = ((Double) rvalue).doubleValue();
					if(lconstant + rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant + rconstant), constraints);
				}
			}
			else {
				double lconstant = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(lconstant + rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant + rconstant), constraints);
				}
				else {
					double rconstant = ((Double) rvalue).doubleValue();
					if(lconstant + rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant + rconstant), constraints);
				}
			}
		}
		else {
			StateConstraints constraint = new StateConstraints(true);
			output.put(graph.get_error_set().chg_numb(expression), constraint);
		}
	}
	
	/* (+, %) */
	/**
	 * roperand = 0 --> failure
	 * x = k * y --> set_numb(0)
	 * y != 0    --> chg_numb
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_add_to_arith_mod(CirExpression expression, CirExpression loperand, CirExpression 
			roperand, StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lop = this.derive_sym_constraint(SymFactory.parse(loperand));
		SymExpression rop = this.derive_sym_constraint(SymFactory.parse(roperand));
		
		if(lop instanceof SymConstant && rop instanceof SymConstant) {
			StateConstraints constraints = new StateConstraints(true);
			Object lvalue = this.get_constant_value(((SymConstant) lop).get_constant());
			Object rvalue = this.get_constant_value(((SymConstant) rop).get_constant());
			if(lvalue instanceof Long) {
				long lconstant = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(rconstant == 0)
						output.put(graph.get_error_set().failure(), constraints);
					else if(lconstant + rconstant != lconstant % rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant % rconstant), constraints);
				}
				else {
					output.put(graph.get_error_set().syntax_error(), constraints);
				}
			}
			else {
				output.put(graph.get_error_set().syntax_error(), constraints);
			}
		}
		else {
			SymMultiExpression y2 = SymFactory.new_multiple_expression(roperand.get_data_type(), COperator.arith_mul);
			y2.add_operand(SymFactory.new_address(StateInfection.AnyInteger, CBasicTypeImpl.int_type));
			y2.add_operand(SymFactory.parse(roperand));
			
			SymExpression condition1 = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.equal_with, SymFactory.parse(roperand), SymFactory.new_constant(0L));
			SymExpression condition2 = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.equal_with, SymFactory.parse(loperand), y2);
			SymExpression condition3 = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, SymFactory.parse(roperand), SymFactory.new_constant(0L));
			
			/* y = 0 --> failure */
			StateConstraints constraint1 = new StateConstraints(true);
			constraint1.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition1));
			output.put(graph.get_error_set().failure(), constraint1);
			
			/* x = 2y --> set_numb */
			StateConstraints constraint2 = new StateConstraints(true);
			constraint2.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition2));
			output.put(graph.get_error_set().set_numb(expression, 0L), constraint2);
			
			/* y != 0 --> chg_numb */
			StateConstraints constraint3 = new StateConstraints(true);
			constraint3.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition3));
			output.put(graph.get_error_set().chg_numb(expression), constraint3);
		}
	}
	/**
	 * --> chg_mod
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_mod_to_arith_add(CirExpression expression, CirExpression loperand, CirExpression 
			roperand, StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lop = this.derive_sym_constraint(SymFactory.parse(loperand));
		SymExpression rop = this.derive_sym_constraint(SymFactory.parse(roperand));
		StateConstraints constraints = new StateConstraints(true);
		
		if(lop instanceof SymConstant && rop instanceof SymConstant) {
			Object lvalue = this.get_constant_value(((SymConstant) lop).get_constant());
			Object rvalue = this.get_constant_value(((SymConstant) rop).get_constant());
			if(lvalue instanceof Long) {
				long lconstant = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(lconstant + rconstant != lconstant % rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant + rconstant), constraints);
				}
				else {
					output.put(graph.get_error_set().syntax_error(), constraints);
				}
			}
			else {
				output.put(graph.get_error_set().syntax_error(), constraints);
			}
		}
		else {
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}
	
	/* (-, *) */
	/**
	 * {x != 0, y != 0} --> chg_numb
	 * {x = 0 or y = 0} --> set_numb
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_sub_to_arith_mul(CirExpression expression, CirExpression loperand, CirExpression 
			roperand, StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lop = this.derive_sym_constraint(SymFactory.parse(loperand));
		SymExpression rop = this.derive_sym_constraint(SymFactory.parse(roperand));
		
		if(lop instanceof SymConstant && rop instanceof SymConstant) {
			StateConstraints constraints = new StateConstraints(true);
			Object lvalue = this.get_constant_value(((SymConstant) lop).get_constant());
			Object rvalue = this.get_constant_value(((SymConstant) rop).get_constant());
			if(lvalue instanceof Long) {
				long lconstant = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(lconstant - rconstant != lconstant * rconstant) {
						output.put(graph.get_error_set().set_numb(expression, lconstant * rconstant), constraints);
					}
				}
				else {
					double rconstant = ((Double) rvalue).doubleValue();
					if(lconstant - rconstant != lconstant * rconstant) {
						output.put(graph.get_error_set().set_numb(expression, lconstant * rconstant), constraints);
					}
				}
			}
			else {
				double lconstant = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(lconstant - rconstant != lconstant * rconstant) {
						output.put(graph.get_error_set().set_numb(expression, lconstant * rconstant), constraints);
					}
				}
				else {
					double rconstant = ((Double) rvalue).doubleValue();
					if(lconstant - rconstant != lconstant * rconstant) {
						output.put(graph.get_error_set().set_numb(expression, lconstant * rconstant), constraints);
					}
				}
			}
		}
		else {
			SymExpression lcondition, rcondition; StateConstraints constraints;
			
			/* {x != 0, y != 0} ==> chg_numb */
			lcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, SymFactory.parse(loperand), SymFactory.new_constant(0L));
			rcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, SymFactory.parse(roperand), SymFactory.new_constant(0L));
			constraints = new StateConstraints(true);
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(lcondition));
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(rcondition));
			output.put(graph.get_error_set().chg_numb(expression), constraints);
			
			/* {x = 0 or y = 0} ==> set_numb */
			lcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.equal_with, SymFactory.parse(loperand), SymFactory.new_constant(0L));
			rcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.equal_with, SymFactory.parse(roperand), SymFactory.new_constant(0L));
			constraints = new StateConstraints(false);
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(lcondition));
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(rcondition));
			output.put(graph.get_error_set().set_numb(expression, 0L), constraints);
		}
	}
	/**
	 * {x != 0, y != 0} ==> chg_numb
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_mul_to_arith_sub(CirExpression expression, CirExpression loperand, CirExpression 
			roperand, StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lop = this.derive_sym_constraint(SymFactory.parse(loperand));
		SymExpression rop = this.derive_sym_constraint(SymFactory.parse(roperand));
		
		if(lop instanceof SymConstant && rop instanceof SymConstant) {
			StateConstraints constraints = new StateConstraints(true);
			Object lvalue = this.get_constant_value(((SymConstant) lop).get_constant());
			Object rvalue = this.get_constant_value(((SymConstant) rop).get_constant());
			if(lvalue instanceof Long) {
				long lconstant = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(lconstant - rconstant != lconstant * rconstant) {
						output.put(graph.get_error_set().set_numb(expression, lconstant - rconstant), constraints);
					}
				}
				else {
					double rconstant = ((Double) rvalue).doubleValue();
					if(lconstant - rconstant != lconstant * rconstant) {
						output.put(graph.get_error_set().set_numb(expression, lconstant - rconstant), constraints);
					}
				}
			}
			else {
				double lconstant = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(lconstant - rconstant != lconstant * rconstant) {
						output.put(graph.get_error_set().set_numb(expression, lconstant - rconstant), constraints);
					}
				}
				else {
					double rconstant = ((Double) rvalue).doubleValue();
					if(lconstant - rconstant != lconstant * rconstant) {
						output.put(graph.get_error_set().set_numb(expression, lconstant - rconstant), constraints);
					}
				}
			}
		}
		else {
			SymExpression lcondition, rcondition; StateConstraints constraints;
			
			/* {x != 0, y != 0} ==> chg_numb */
			lcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, SymFactory.parse(loperand), SymFactory.new_constant(0L));
			rcondition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, SymFactory.parse(roperand), SymFactory.new_constant(0L));
			constraints = new StateConstraints(true);
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(lcondition));
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(rcondition));
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}
	
	/* (-, /) */
	/**
	 * roperand = 0 --> failure
	 * loperand = 0 --> set_numb
	 * roperand != 0--> chg_numb
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_sub_to_arith_div(CirExpression expression, CirExpression loperand, CirExpression 
			roperand, StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lop = this.derive_sym_constraint(SymFactory.parse(loperand));
		SymExpression rop = this.derive_sym_constraint(SymFactory.parse(roperand));
		
		if(lop instanceof SymConstant && rop instanceof SymConstant) {
			StateConstraints constraints = new StateConstraints(true);
			Object lvalue = this.get_constant_value(((SymConstant) lop).get_constant());
			Object rvalue = this.get_constant_value(((SymConstant) rop).get_constant());
			if(lvalue instanceof Long) {
				long lconstant = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(rconstant == 0)
						output.put(graph.get_error_set().failure(), constraints);
					else if(lconstant - rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant / rconstant), constraints);
				}
				else {
					double rconstant = ((Double) rvalue).doubleValue();
					if(rconstant == 0)
						output.put(graph.get_error_set().failure(), constraints);
					else if(lconstant - rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant / rconstant), constraints);
				}
			}
			else {
				double lconstant = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(rconstant == 0)
						output.put(graph.get_error_set().failure(), constraints);
					else if(lconstant - rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant / rconstant), constraints);
				}
				else {
					double rconstant = ((Double) rvalue).doubleValue();
					if(rconstant == 0)
						output.put(graph.get_error_set().failure(), constraints);
					else if(lconstant - rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant / rconstant), constraints);
				}
			}
		}
		else {
			SymExpression condition; StateConstraints constraints;
			
			/* roperand = 0 --> failure */
			condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.equal_with, SymFactory.parse(roperand), SymFactory.new_constant(0L));
			constraints = new StateConstraints(true);
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition));
			output.put(graph.get_error_set().failure(), constraints);
			
			/* loperand = 0 --> set_numb */
			condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.equal_with, SymFactory.parse(loperand), SymFactory.new_constant(0L));
			constraints = new StateConstraints(true);
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition));
			output.put(graph.get_error_set().set_numb(expression, 0L), constraints);
			
			/* roperand != 0--> chg_numb */
			condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, SymFactory.parse(roperand), SymFactory.new_constant(0L));
			constraints = new StateConstraints(true);
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition));
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
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
	private void arith_div_to_arith_sub(CirExpression expression, CirExpression loperand, CirExpression 
			roperand, StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lop = this.derive_sym_constraint(SymFactory.parse(loperand));
		SymExpression rop = this.derive_sym_constraint(SymFactory.parse(roperand));
		
		if(lop instanceof SymConstant && rop instanceof SymConstant) {
			StateConstraints constraints = new StateConstraints(true);
			Object lvalue = this.get_constant_value(((SymConstant) lop).get_constant());
			Object rvalue = this.get_constant_value(((SymConstant) rop).get_constant());
			if(lvalue instanceof Long) {
				long lconstant = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(lconstant - rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant - rconstant), constraints);
				}
				else {
					double rconstant = ((Double) rvalue).doubleValue();
					if(lconstant - rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant - rconstant), constraints);
				}
			}
			else {
				double lconstant = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(lconstant - rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant - rconstant), constraints);
				}
				else {
					double rconstant = ((Double) rvalue).doubleValue();
					if(lconstant - rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant - rconstant), constraints);
				}
			}
		}
		else {
			output.put(graph.get_error_set().chg_numb(expression), new StateConstraints(true));
		}
	}
	
	/* (-, %) */
	/**
	 * roperand = 0 --> failure
	 * x = k * y --> set_numb(0)
	 * y != 0    --> chg_numb
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_sub_to_arith_mod(CirExpression expression, CirExpression loperand, CirExpression 
			roperand, StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lop = this.derive_sym_constraint(SymFactory.parse(loperand));
		SymExpression rop = this.derive_sym_constraint(SymFactory.parse(roperand));
		
		if(lop instanceof SymConstant && rop instanceof SymConstant) {
			StateConstraints constraints = new StateConstraints(true);
			Object lvalue = this.get_constant_value(((SymConstant) lop).get_constant());
			Object rvalue = this.get_constant_value(((SymConstant) rop).get_constant());
			if(lvalue instanceof Long) {
				long lconstant = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(rconstant == 0)
						output.put(graph.get_error_set().failure(), constraints);
					else if(lconstant - rconstant != lconstant % rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant % rconstant), constraints);
				}
				else {
					output.put(graph.get_error_set().syntax_error(), constraints);
				}
			}
			else {
				output.put(graph.get_error_set().syntax_error(), constraints);
			}
		}
		else {
			SymMultiExpression y2 = SymFactory.new_multiple_expression(roperand.get_data_type(), COperator.arith_mul);
			y2.add_operand(SymFactory.new_address(StateInfection.AnyInteger, CBasicTypeImpl.int_type));
			y2.add_operand(SymFactory.parse(roperand));
			
			SymExpression condition1 = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.equal_with, SymFactory.parse(roperand), SymFactory.new_constant(0L));
			SymExpression condition2 = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.equal_with, SymFactory.parse(loperand), y2);
			SymExpression condition3 = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, SymFactory.parse(roperand), SymFactory.new_constant(0L));
			
			/* y = 0 --> failure */
			StateConstraints constraint1 = new StateConstraints(true);
			constraint1.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition1));
			output.put(graph.get_error_set().failure(), constraint1);
			
			/* x = 2y --> set_numb */
			StateConstraints constraint2 = new StateConstraints(true);
			constraint2.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition2));
			output.put(graph.get_error_set().set_numb(expression, 0L), constraint2);
			
			/* y != 0 --> chg_numb */
			StateConstraints constraint3 = new StateConstraints(true);
			constraint3.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition3));
			output.put(graph.get_error_set().chg_numb(expression), constraint3);
		}
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
	private void arith_mod_to_arith_sub(CirExpression expression, CirExpression loperand, CirExpression 
			roperand, StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lop = this.derive_sym_constraint(SymFactory.parse(loperand));
		SymExpression rop = this.derive_sym_constraint(SymFactory.parse(roperand));
		StateConstraints constraints = new StateConstraints(true);
		
		if(lop instanceof SymConstant && rop instanceof SymConstant) {
			Object lvalue = this.get_constant_value(((SymConstant) lop).get_constant());
			Object rvalue = this.get_constant_value(((SymConstant) rop).get_constant());
			if(lvalue instanceof Long) {
				long lconstant = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(lconstant - rconstant != lconstant % rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant + rconstant), constraints);
				}
				else {
					output.put(graph.get_error_set().syntax_error(), constraints);
				}
			}
			else {
				output.put(graph.get_error_set().syntax_error(), constraints);
			}
		}
		else {
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}
	
	/* (*, /) */
	/**
	 * y == 0
	 * x != 0
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_mul_to_arith_div(CirExpression expression, CirExpression loperand, CirExpression 
			roperand, StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lop = this.derive_sym_constraint(SymFactory.parse(loperand));
		SymExpression rop = this.derive_sym_constraint(SymFactory.parse(roperand));
		
		if(lop instanceof SymConstant && rop instanceof SymConstant) {
			StateConstraints constraints = new StateConstraints(true);
			Object lvalue = this.get_constant_value(((SymConstant) lop).get_constant());
			Object rvalue = this.get_constant_value(((SymConstant) rop).get_constant());
			if(lvalue instanceof Long) {
				long lconstant = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(rconstant == 0)
						output.put(graph.get_error_set().failure(), constraints);
					else if(lconstant * rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant / rconstant), constraints);
				}
				else {
					double rconstant = ((Double) rvalue).doubleValue();
					if(rconstant == 0)
						output.put(graph.get_error_set().failure(), constraints);
					else if(lconstant * rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant / rconstant), constraints);
				}
			}
			else {
				double lconstant = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(rconstant == 0)
						output.put(graph.get_error_set().failure(), constraints);
					else if(lconstant * rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant / rconstant), constraints);
				}
				else {
					double rconstant = ((Double) rvalue).doubleValue();
					if(rconstant == 0)
						output.put(graph.get_error_set().failure(), constraints);
					else if(lconstant * rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant / rconstant), constraints);
				}
			}
		}
		else {
			SymExpression condition; StateConstraints constraints;
			/* y = 0 */
			condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.equal_with, SymFactory.parse(roperand), SymFactory.new_constant(0L));
			constraints = new StateConstraints(true);
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition));
			output.put(graph.get_error_set().failure(), constraints);
			/* x != 0 */
			condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, SymFactory.parse(loperand), SymFactory.new_constant(0L));
			constraints = new StateConstraints(true);
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition));
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}
	/**
	 * x != 0
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_div_to_arith_mul(CirExpression expression, CirExpression loperand, CirExpression 
			roperand, StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lop = this.derive_sym_constraint(SymFactory.parse(loperand));
		SymExpression rop = this.derive_sym_constraint(SymFactory.parse(roperand));
		
		if(lop instanceof SymConstant && rop instanceof SymConstant) {
			StateConstraints constraints = new StateConstraints(true);
			Object lvalue = this.get_constant_value(((SymConstant) lop).get_constant());
			Object rvalue = this.get_constant_value(((SymConstant) rop).get_constant());
			if(lvalue instanceof Long) {
				long lconstant = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(rconstant == 0)
						output.put(graph.get_error_set().failure(), constraints);
					else if(lconstant * rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant * rconstant), constraints);
				}
				else {
					double rconstant = ((Double) rvalue).doubleValue();
					if(rconstant == 0)
						output.put(graph.get_error_set().failure(), constraints);
					else if(lconstant * rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant * rconstant), constraints);
				}
			}
			else {
				double lconstant = ((Double) lvalue).doubleValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(rconstant == 0)
						output.put(graph.get_error_set().failure(), constraints);
					else if(lconstant * rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant * rconstant), constraints);
				}
				else {
					double rconstant = ((Double) rvalue).doubleValue();
					if(rconstant == 0)
						output.put(graph.get_error_set().failure(), constraints);
					else if(lconstant * rconstant != lconstant / rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant * rconstant), constraints);
				}
			}
		}
		else {
			SymExpression condition; StateConstraints constraints;
			/* x != 0 */
			condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, SymFactory.parse(loperand), SymFactory.new_constant(0L));
			constraints = new StateConstraints(true);
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition));
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}
	
	/* (*, %) */
	/**
	 * y == 0
	 * x != 0
	 * x = k*y
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_mul_to_arith_mod(CirExpression expression, CirExpression loperand, CirExpression 
			roperand, StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lop = this.derive_sym_constraint(SymFactory.parse(loperand));
		SymExpression rop = this.derive_sym_constraint(SymFactory.parse(roperand));
		
		if(lop instanceof SymConstant && rop instanceof SymConstant) {
			StateConstraints constraints = new StateConstraints(true);
			Object lvalue = this.get_constant_value(((SymConstant) lop).get_constant());
			Object rvalue = this.get_constant_value(((SymConstant) rop).get_constant());
			if(lvalue instanceof Long) {
				long lconstant = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(rconstant == 0)
						output.put(graph.get_error_set().failure(), constraints);
					else if(lconstant * rconstant != lconstant % rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant % rconstant), constraints);
				}
				else {
					output.put(graph.get_error_set().syntax_error(), constraints);
				}
			}
			else {
				output.put(graph.get_error_set().syntax_error(), constraints);
			}
		}
		else {
			SymExpression condition; StateConstraints constraints;
			
			/* y = 0 */
			condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.equal_with, SymFactory.parse(roperand), SymFactory.new_constant(0L));
			constraints = new StateConstraints(true);
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition));
			output.put(graph.get_error_set().failure(), constraints);
			
			/* x != 0 */
			condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, SymFactory.parse(loperand), SymFactory.new_constant(0L));
			constraints = new StateConstraints(true);
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition));
			output.put(graph.get_error_set().chg_numb(expression), constraints);
			
			/* x = k * y */
			SymMultiExpression yk = SymFactory.new_multiple_expression(roperand.get_data_type(), COperator.arith_mul);
			yk.add_operand(SymFactory.parse(roperand)); 
			yk.add_operand(SymFactory.new_address(StateInfection.AnyInteger, CBasicTypeImpl.int_type));
			condition = SymFactory.new_binary_expression(
						CBasicTypeImpl.bool_type, COperator.equal_with, SymFactory.parse(loperand), yk);
			constraints = new StateConstraints(true);
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition));
			output.put(graph.get_error_set().set_numb(expression, 0L), constraints);
		}
	}
	/**
	 * x != 0
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_mod_to_arith_mul(CirExpression expression, CirExpression loperand, CirExpression 
			roperand, StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lop = this.derive_sym_constraint(SymFactory.parse(loperand));
		SymExpression rop = this.derive_sym_constraint(SymFactory.parse(roperand));
		
		if(lop instanceof SymConstant && rop instanceof SymConstant) {
			StateConstraints constraints = new StateConstraints(true);
			Object lvalue = this.get_constant_value(((SymConstant) lop).get_constant());
			Object rvalue = this.get_constant_value(((SymConstant) rop).get_constant());
			if(lvalue instanceof Long) {
				long lconstant = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(rconstant == 0)
						output.put(graph.get_error_set().failure(), constraints);
					else if(lconstant * rconstant != lconstant % rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant % rconstant), constraints);
				}
				else {
					output.put(graph.get_error_set().syntax_error(), constraints);
				}
			}
			else {
				output.put(graph.get_error_set().syntax_error(), constraints);
			}
		}
		else {
			SymExpression condition; StateConstraints constraints;
			
			/* x != 0 */
			condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, SymFactory.parse(loperand), SymFactory.new_constant(0L));
			constraints = new StateConstraints(true);
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition));
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}
	
	/* (/, %) */
	/**
	 * x != 0
	 * x = k * y
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_div_to_arith_mod(CirExpression expression, CirExpression loperand, CirExpression 
			roperand, StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lop = this.derive_sym_constraint(SymFactory.parse(loperand));
		SymExpression rop = this.derive_sym_constraint(SymFactory.parse(roperand));
		
		if(lop instanceof SymConstant && rop instanceof SymConstant) {
			StateConstraints constraints = new StateConstraints(true);
			Object lvalue = this.get_constant_value(((SymConstant) lop).get_constant());
			Object rvalue = this.get_constant_value(((SymConstant) rop).get_constant());
			if(lvalue instanceof Long) {
				long lconstant = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(lconstant / rconstant != lconstant % rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant % rconstant), constraints);
				}
				else {
					output.put(graph.get_error_set().syntax_error(), constraints);
				}
			}
			else {
				output.put(graph.get_error_set().syntax_error(), constraints);
			}
		}
		else {
			SymExpression condition; StateConstraints constraints;
			
			/* x != 0 */
			condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, SymFactory.parse(loperand), SymFactory.new_constant(0L));
			constraints = new StateConstraints(true);
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition));
			output.put(graph.get_error_set().chg_numb(expression), constraints);
			
			/* x = k * y */
			SymMultiExpression yk = SymFactory.new_multiple_expression(roperand.get_data_type(), COperator.arith_mul);
			yk.add_operand(SymFactory.parse(roperand)); 
			yk.add_operand(SymFactory.new_address(StateInfection.AnyInteger, CBasicTypeImpl.int_type));
			condition = SymFactory.new_binary_expression(
						CBasicTypeImpl.bool_type, COperator.equal_with, SymFactory.parse(loperand), yk);
			constraints = new StateConstraints(true);
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition));
			output.put(graph.get_error_set().set_numb(expression, 0L), constraints);
		}
	}
	/**
	 * x != 0
	 * @param expression
	 * @param loperand
	 * @param roperand
	 * @param graph
	 * @param output
	 * @throws Exception
	 */
	private void arith_mod_to_arith_div(CirExpression expression, CirExpression loperand, CirExpression 
			roperand, StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lop = this.derive_sym_constraint(SymFactory.parse(loperand));
		SymExpression rop = this.derive_sym_constraint(SymFactory.parse(roperand));
		
		if(lop instanceof SymConstant && rop instanceof SymConstant) {
			StateConstraints constraints = new StateConstraints(true);
			Object lvalue = this.get_constant_value(((SymConstant) lop).get_constant());
			Object rvalue = this.get_constant_value(((SymConstant) rop).get_constant());
			if(lvalue instanceof Long) {
				long lconstant = ((Long) lvalue).longValue();
				if(rvalue instanceof Long) {
					long rconstant = ((Long) rvalue).longValue();
					if(lconstant / rconstant != lconstant % rconstant)
						output.put(graph.get_error_set().set_numb(expression, lconstant / rconstant), constraints);
				}
				else {
					output.put(graph.get_error_set().syntax_error(), constraints);
				}
			}
			else {
				output.put(graph.get_error_set().syntax_error(), constraints);
			}
		}
		else {
			SymExpression condition; StateConstraints constraints;
			
			/* x != 0 */
			condition = SymFactory.new_binary_expression(CBasicTypeImpl.bool_type, 
					COperator.not_equals, SymFactory.parse(loperand), SymFactory.new_constant(0L));
			constraints = new StateConstraints(true);
			constraints.add_constraint(expression.statement_of(), this.derive_sym_constraint(condition));
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}
	
}
