package com.jcsa.jcmuta.mutant.error2mutation.process;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateProcess;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.symb.StateConstraints;

public class NEGProcess extends StateProcess {

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
		this.propagate_set_numb(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_chg_bool(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_chg_numb(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_set_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = (CirExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object value = this.get_number(error.get_operand(1));
		
		if(value instanceof Long) {
			output.put(graph.get_error_set().set_numb(expression,-((Long) value).longValue()), constraints);
		}
		else {
			output.put(graph.get_error_set().set_numb(expression,-((Double) value).doubleValue()), constraints);
		}
	}

	@Override
	protected void propagate_neg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = (CirExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		output.put(graph.get_error_set().neg_numb(expression), constraints);
	}

	@Override
	protected void propagate_xor_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_chg_numb(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_rsv_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = (CirExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		output.put(graph.get_error_set().dif_numb(expression, 1L), constraints);
	}

	@Override
	protected void propagate_dif_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = (CirExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object difference = this.get_number(error.get_operand(1));
		
		if(!StateEvaluation.is_zero_number(difference)) {
			if(difference instanceof Long) {
				output.put(graph.get_error_set().dif_numb(expression,
						-((Long) difference).longValue()), constraints);
			}
			else {
				output.put(graph.get_error_set().dif_numb(expression,
						-((Double) difference).doubleValue()), constraints);
			}
		}
	}

	@Override
	protected void propagate_inc_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = (CirExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		output.put(graph.get_error_set().dec_numb(expression), constraints);
	}

	@Override
	protected void propagate_dec_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = (CirExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		output.put(graph.get_error_set().inc_numb(expression), constraints);
	}

	@Override
	protected void propagate_chg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = (CirExpression) cir_target;
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
		this.propagate_chg_numb(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_mut_refer(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_mut_expr(error, cir_target, graph, output);
	}

}
