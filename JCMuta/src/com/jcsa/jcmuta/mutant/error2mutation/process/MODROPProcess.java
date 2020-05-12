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

public class MODROPProcess extends StateProcess {

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
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Boolean roperand = (Boolean) error.get_operand(1);
		
		/** constant left-operand **/
		if(!roperand) {
			output.put(graph.get_error_set().failure(), constraints);
		}
		else {
			output.put(graph.get_error_set().set_numb(expression, 0L), constraints);
		}
	}

	@Override
	protected void propagate_chg_bool(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		output.put(graph.get_error_set().set_numb(expression, 0L), constraints);
	}

	@Override
	protected void propagate_set_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object loperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		Object roperand = error.get_operand(1);
		long rvalue = (Long) this.get_number(roperand);
		
		if(StateEvaluation.is_zero_number(roperand)) {
			output.put(graph.get_error_set().failure(), constraints);
		}
		else if(rvalue == 1 || rvalue == -1) {
			output.put(graph.get_error_set().set_numb(expression, 0L), constraints);
		}
		else if(!(loperand instanceof SymExpression)) {
			long lvalue = (Long) this.get_number(loperand);
			if(lvalue >= -1 && lvalue <= 1) { return; /** equivalent mutant detected **/ }
			else {
				Long result = (Long) this.arith_mod(loperand, roperand);
				output.put(graph.get_error_set().set_numb(expression, result), constraints);
			}
		}
		else {
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_neg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		return;
	}

	@Override
	protected void propagate_xor_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_chg_numb(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_rsv_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_chg_numb(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_dif_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_chg_numb(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_inc_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_chg_numb(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_dec_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_chg_numb(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_chg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object loperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		if(!(loperand instanceof SymExpression)) {
			long lvalue = (Long) this.get_number(loperand);
			if(lvalue >= -1 && lvalue <= 1) { return; /** equivalence detected **/ }
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
		else {
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
		this.propagate_chg_numb(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_mut_refer(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_mut_expr(error, cir_target, graph, output);
	}

}
