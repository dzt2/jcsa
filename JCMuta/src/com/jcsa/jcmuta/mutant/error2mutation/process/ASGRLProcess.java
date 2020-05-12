package com.jcsa.jcmuta.mutant.error2mutation.process;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateProcess;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.symb.StateConstraints;

/**
 * From right value to left value in assignment.
 * 
 * @author yukimula
 *
 */
public class ASGRLProcess extends StateProcess {

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
		CirExpression lvalue = (CirExpression) cir_target;
		Boolean parameter = (Boolean) error.get_operand(1);
		output.put(graph.get_error_set().set_bool(lvalue, parameter.booleanValue()), 
				StateEvaluation.get_conjunctions());
	}

	@Override
	protected void propagate_chg_bool(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		output.put(graph.get_error_set().chg_bool(lvalue), StateEvaluation.get_conjunctions());
	}

	@Override
	protected void propagate_set_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		Object parameter = error.get_operand(1);
		
		if(parameter instanceof Long) {
			output.put(graph.get_error_set().set_numb(lvalue, ((Long) parameter).longValue()), 
					StateEvaluation.get_conjunctions());
		}
		else {
			output.put(graph.get_error_set().set_numb(lvalue, ((Double) parameter).doubleValue()), 
					StateEvaluation.get_conjunctions());
		}
	}

	@Override
	protected void propagate_neg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		output.put(graph.get_error_set().neg_numb(lvalue), StateEvaluation.get_conjunctions());
	}

	@Override
	protected void propagate_xor_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		Long parameter = (Long) error.get_operand(1);
		output.put(graph.get_error_set().xor_numb(lvalue, parameter.longValue()), 
				StateEvaluation.get_conjunctions());
	}

	@Override
	protected void propagate_rsv_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		output.put(graph.get_error_set().rsv_numb(lvalue), StateEvaluation.get_conjunctions());
	}

	@Override
	protected void propagate_dif_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		Object parameter = error.get_operand(1);
		
		if(parameter instanceof Long) {
			output.put(graph.get_error_set().dif_numb(lvalue, ((Long) parameter).longValue()), 
					StateEvaluation.get_conjunctions());
		}
		else {
			output.put(graph.get_error_set().dif_numb(lvalue, ((Double) parameter).doubleValue()), 
					StateEvaluation.get_conjunctions());
		}
	}

	@Override
	protected void propagate_inc_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		output.put(graph.get_error_set().inc_numb(lvalue), StateEvaluation.get_conjunctions());
	}

	@Override
	protected void propagate_dec_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		output.put(graph.get_error_set().dec_numb(lvalue), StateEvaluation.get_conjunctions());
	}

	@Override
	protected void propagate_chg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		output.put(graph.get_error_set().chg_numb(lvalue), StateEvaluation.get_conjunctions());
	}

	@Override
	protected void propagate_dif_addr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		Long parameter = (Long) error.get_operand(1);
		output.put(graph.get_error_set().dif_addr(lvalue, ((Long) parameter).longValue()), 
				StateEvaluation.get_conjunctions());
	}

	@Override
	protected void propagate_set_addr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		String parameter = (String) error.get_operand(1);
		output.put(graph.get_error_set().set_addr(lvalue, parameter), 
				StateEvaluation.get_conjunctions());
	}

	@Override
	protected void propagate_chg_addr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		output.put(graph.get_error_set().chg_addr(lvalue), StateEvaluation.get_conjunctions());
	}

	@Override
	protected void propagate_mut_expr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		output.put(graph.get_error_set().mut_expr(lvalue), StateEvaluation.get_conjunctions());
	}

	@Override
	protected void propagate_mut_refer(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression lvalue = (CirExpression) cir_target;
		output.put(graph.get_error_set().mut_expr(lvalue), StateEvaluation.get_conjunctions());
	}

}
