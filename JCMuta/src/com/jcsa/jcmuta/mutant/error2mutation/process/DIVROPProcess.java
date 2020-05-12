package com.jcsa.jcmuta.mutant.error2mutation.process;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateProcess;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

public class DIVROPProcess extends StateProcess {

	@Override
	protected void propagate_execute(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
	}

	@Override
	protected void propagate_not_execute(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
	}

	@Override
	protected void propagate_execute_for(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
	}

	@Override
	protected void propagate_set_bool(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Boolean roperand = ((Boolean) error.get_operand(1)).booleanValue();
		if(!roperand) output.put(graph.get_error_set().failure(), constraints);
	}

	@Override
	protected void propagate_chg_bool(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		output.put(graph.get_error_set().failure(), constraints);
	}

	@Override
	protected void propagate_set_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object loperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		Object roperand = error.get_operand(1);
		
		/** CASE-0. when divide is zero **/
		if(StateEvaluation.is_zero_number(roperand)) {
			output.put(graph.get_error_set().failure(), constraints);
		}
		/** CASE-1. constant left-operand **/
		else if(!(loperand instanceof SymExpression)) {
			/** equivalent mutant detected **/
			if(StateEvaluation.is_zero_number(loperand)) { return; }
			else {
				Object result = this.arith_div(loperand, roperand);
				if(result instanceof Long) {
					output.put(graph.get_error_set().set_numb(expression, ((Long) result).longValue()), constraints);
				}
				else {
					output.put(graph.get_error_set().set_numb(expression, ((Double) result).doubleValue()), constraints);
				}
			}
		}
		/** CASE-2. dynamic left-operand **/
		else {
			SymExpression constraint = 
					StateEvaluation.new_condition(expression.get_operand(0), true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_neg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object loperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		if(!(loperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(loperand)) { return; }
			else {
				output.put(graph.get_error_set().neg_numb(expression), constraints);
			}
		}
		else {
			SymExpression constraint = 
					StateEvaluation.new_condition(expression.get_operand(0), true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().neg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_xor_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object loperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		if(!(loperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(loperand)) { return; }
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
		else {
			SymExpression constraint = 
					StateEvaluation.new_condition(expression.get_operand(0), true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_rsv_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object loperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		if(!(loperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(loperand)) { return; }
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
		else {
			SymExpression constraint = 
					StateEvaluation.new_condition(expression.get_operand(0), true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_dif_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object loperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		Object roperand = error.get_operand(1);
		
		if(!(loperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(loperand)) { return; }
			else if(StateEvaluation.is_positive_number(loperand)) {
				if(StateEvaluation.is_positive_number(roperand)) {
					output.put(graph.get_error_set().dec_numb(expression), constraints);
				}
				else if(StateEvaluation.is_negative_number(roperand)) {
					output.put(graph.get_error_set().inc_numb(expression), constraints);
				}
			}
			else if(StateEvaluation.is_negative_number(roperand)) {
				if(StateEvaluation.is_positive_number(roperand)) {
					output.put(graph.get_error_set().inc_numb(expression), constraints);
				}
				else {
					output.put(graph.get_error_set().dec_numb(expression), constraints);
				}
			}
		}
		else {
			SymExpression constraint = 
					StateEvaluation.new_condition(expression.get_operand(0), true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_inc_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object loperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		if(!(loperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(loperand)) { return; }
			else if(StateEvaluation.is_positive_number(loperand)) {
				output.put(graph.get_error_set().dec_numb(expression), constraints);
			}
			else {
				output.put(graph.get_error_set().inc_numb(expression), constraints);
			}
		}
		else {
			SymExpression constraint = 
					StateEvaluation.new_condition(expression.get_operand(0), true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_dec_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object loperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		if(!(loperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(loperand)) { return; }
			else if(StateEvaluation.is_positive_number(loperand)) {
				output.put(graph.get_error_set().inc_numb(expression), constraints);
			}
			else {
				output.put(graph.get_error_set().dec_numb(expression), constraints);
			}
		}
		else {
			SymExpression constraint = 
					StateEvaluation.new_condition(expression.get_operand(0), true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_chg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object loperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		if(!(loperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(loperand)) { return; }
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
		else {
			SymExpression constraint = 
					StateEvaluation.new_condition(expression.get_operand(0), true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_dif_addr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_dif_numb(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_set_addr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_set_numb(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_chg_addr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_chg_numb(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_mut_expr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object loperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		if(!(loperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(loperand)) { return; }
			else {
				output.put(graph.get_error_set().mut_expr(expression), constraints);
			}
		}
		else {
			SymExpression constraint = 
					StateEvaluation.new_condition(expression.get_operand(0), true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().mut_expr(expression), constraints);
		}
	}

	@Override
	protected void propagate_mut_refer(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_mut_expr(error, cir_target, graph, output);
	}

}
