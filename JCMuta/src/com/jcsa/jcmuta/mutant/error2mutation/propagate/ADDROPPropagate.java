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

public class ADDROPPropagate extends StatePropagate {

	@Override
	protected void propagate_execute(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		return;
	}

	@Override
	protected void propagate_not_execute(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		return;
	}

	@Override
	protected void propagate_execute_for(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		return;
	}

	@Override
	protected void propagate_set_bool(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		boolean parameter = ((Boolean) error.get_operand(1)).booleanValue();
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		if(parameter) 
			output.put(graph.get_error_set().dif_numb(expression, 1L), constraints);
		else 
			output.put(graph.get_error_set().dif_numb(expression,-1L), constraints);
	}

	@Override
	protected void propagate_chg_bool(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		output.put(graph.get_error_set().chg_numb(expression), constraints);
	}

	@Override
	protected void propagate_set_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		Object roperand = error.get_operand(1);
		Object loperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		if(!(roperand instanceof SymExpression)) {
			Object result = this.arith_add(loperand, roperand);
			if(result instanceof Long) {
				output.put(graph.get_error_set().set_numb(expression, ((Long) result).longValue()), constraints);
			}
			else {
				output.put(graph.get_error_set().set_numb(expression, ((Double)result).doubleValue()), constraints);
			}
		}
		else {
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
			if(StateEvaluation.is_zero_number(loperand)) {
				output.put(graph.get_error_set().neg_numb(expression), constraints);
			}
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
		else {
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
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
		Object loperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		if(!(loperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(loperand)) {
				output.put(graph.get_error_set().rsv_numb(expression), constraints);
			}
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
		else {
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_dif_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object difference = error.get_operand(1);
		
		if(difference instanceof Long) {
			output.put(graph.get_error_set().dif_numb(expression, ((Long) difference).longValue()), constraints);
		}
		else {
			output.put(graph.get_error_set().dif_numb(expression, ((Double) difference).doubleValue()), constraints);
		}
	}

	@Override
	protected void propagate_inc_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		output.put(graph.get_error_set().inc_numb(expression), constraints);
	}

	@Override
	protected void propagate_dec_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		output.put(graph.get_error_set().dec_numb(expression), constraints);
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
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Long difference = (Long) error.get_operand(1);
		output.put(graph.get_error_set().dif_numb(expression, difference.longValue()), constraints);
	}

	@Override
	protected void propagate_set_addr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		String roperand = (String) error.get_operand(1);
		Object loperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		if(!(loperand instanceof SymExpression)) {
			Object result = this.arith_add(loperand, roperand);
			if(result instanceof Long) {
				if(((Long) result).longValue() == 0L) {
					output.put(graph.get_error_set().set_addr(expression, StateError.NullPointer), constraints);
				}
				else {
					output.put(graph.get_error_set().set_addr(expression, StateError.InvalidAddr), constraints);
				}
			}
			else {
				if(((Double) result).doubleValue() == 0) {
					output.put(graph.get_error_set().set_addr(expression, StateError.NullPointer), constraints);
				}
				else {
					output.put(graph.get_error_set().set_addr(expression, StateError.InvalidAddr), constraints);
				}
			}
		}
		else {
			output.put(graph.get_error_set().chg_addr(expression), constraints);
		}
	}

	@Override
	protected void propagate_chg_addr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		output.put(graph.get_error_set().chg_addr(expression), constraints);
	}

	@Override
	protected void propagate_mut_expr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		output.put(graph.get_error_set().mut_expr(expression), constraints);
	}

	@Override
	protected void propagate_mut_refer(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		output.put(graph.get_error_set().mut_expr(expression), constraints);
	}

}
