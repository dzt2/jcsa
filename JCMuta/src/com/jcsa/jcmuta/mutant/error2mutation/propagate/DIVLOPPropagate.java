package com.jcsa.jcmuta.mutant.error2mutation.propagate;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StatePropagate;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

public class DIVLOPPropagate extends StatePropagate {

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
		/** declarations **/
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		Boolean loperand = ((Boolean) error.get_operand(1)).booleanValue();
		
		/** CASE-1. when right-operand is constant **/
		if(!(roperand instanceof SymExpression)) {
			Object result = this.arith_div(loperand, roperand);
			if(result instanceof Long) {
				output.put(graph.get_error_set().set_numb(expression, ((Long) result).longValue()), constraints);
			}
			else {
				output.put(graph.get_error_set().set_numb(expression, ((Double) result).doubleValue()), constraints);
			}
		}
		/** CASE-2. when right-operand is dynamic **/
		else {
			if(loperand) {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
			else {
				output.put(graph.get_error_set().set_numb(expression, 0L), constraints);
			}
		}
	}

	@Override
	protected void propagate_chg_bool(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/** declarations **/
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		output.put(graph.get_error_set().chg_numb(expression), constraints);
	}

	@Override
	protected void propagate_set_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/** declarations **/
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object loperand = error.get_operand(1);
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		
		/** CASE-1. when right-operand is constant **/
		if(!(roperand instanceof SymExpression)) {
			Object result = this.arith_div(loperand, roperand);
			if(result instanceof Long) {
				output.put(graph.get_error_set().set_numb(expression, ((Long) result).longValue()), constraints);
			}
			else {
				output.put(graph.get_error_set().set_numb(expression, ((Double) result).doubleValue()), constraints);
			}
		}
		/** CASE-2. when right-operand is dynamic **/
		else {
			if(StateEvaluation.is_zero_number(loperand)) {
				output.put(graph.get_error_set().set_numb(expression, 0L), constraints);
			}
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
	}

	@Override
	protected void propagate_neg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		output.put(graph.get_error_set().neg_numb(expression), constraints);
	}

	@Override
	protected void propagate_xor_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		output.put(graph.get_error_set().chg_numb(expression), constraints);
	}

	@Override
	protected void propagate_rsv_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		output.put(graph.get_error_set().chg_numb(expression), constraints);
	}

	@Override
	protected void propagate_dif_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/** declarations **/
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object loperand = error.get_operand(1);
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		
		/** CASE-1. when right-operand is constant **/
		if(!(roperand instanceof SymExpression)) {
			Object result = this.arith_div(loperand, roperand);
			if(result instanceof Long) {
				output.put(graph.get_error_set().dif_numb(expression, ((Long) result).longValue()), constraints);
			}
			else {
				output.put(graph.get_error_set().dif_numb(expression, ((Double) result).doubleValue()), constraints);
			}
		}
		else {
			if(!StateEvaluation.is_zero_number(loperand)) {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
	}

	@Override
	protected void propagate_inc_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/** declarations **/
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		
		/** CASE-1. when right-operand is constant **/
		if(!(roperand instanceof SymExpression)) {
			if(StateEvaluation.is_positive_number(roperand)) {
				output.put(graph.get_error_set().inc_numb(expression), constraints);
			}
			else {
				output.put(graph.get_error_set().dec_numb(expression), constraints);
			}
		}
		else {
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_dec_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/** declarations **/
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		
		/** CASE-1. when right-operand is constant **/
		if(!(roperand instanceof SymExpression)) {
			if(StateEvaluation.is_positive_number(roperand)) {
				output.put(graph.get_error_set().dec_numb(expression), constraints);
			}
			else {
				output.put(graph.get_error_set().inc_numb(expression), constraints);
			}
		}
		else {
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_chg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		output.put(graph.get_error_set().chg_numb(expression), constraints);
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
		output.put(graph.get_error_set().chg_numb(expression), constraints);
	}

	@Override
	protected void propagate_mut_refer(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		output.put(graph.get_error_set().chg_numb(expression), constraints);
	}

}
