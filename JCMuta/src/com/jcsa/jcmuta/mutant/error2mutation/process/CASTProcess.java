package com.jcsa.jcmuta.mutant.error2mutation.process;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateProcess;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

public class CASTProcess extends StateProcess {

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
		CirExpression expression = (CirExpression) cir_target;
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		Boolean parameter = (Boolean) error.get_operand(1);
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		if(CTypeAnalyzer.is_boolean(type)) {
			output.put(graph.get_error_set().set_bool(expression, parameter), constraints);
		}
		else if(CTypeAnalyzer.is_number(type)) {
			if(parameter) {
				output.put(graph.get_error_set().set_numb(expression, 1L), constraints);
			}
			else {
				output.put(graph.get_error_set().set_numb(expression, 0L), constraints);
			}
		}
		else if(CTypeAnalyzer.is_pointer(type)) {
			if(parameter) {
				output.put(graph.get_error_set().set_addr(expression, StateError.InvalidAddr), constraints);
			}
			else {
				output.put(graph.get_error_set().set_addr(expression, StateError.NullPointer), constraints);
			}
		}
	}

	@Override
	protected void propagate_chg_bool(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = (CirExpression) cir_target;
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		if(CTypeAnalyzer.is_boolean(type)) {
			output.put(graph.get_error_set().chg_bool(expression), constraints);
		}
		else if(CTypeAnalyzer.is_number(type)) {
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
		else if(CTypeAnalyzer.is_pointer(type)) {
			output.put(graph.get_error_set().chg_addr(expression), constraints);
		}
	}

	@Override
	protected void propagate_set_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = (CirExpression) cir_target;
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		Object parameter = this.get_number(error.get_operand(1));
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		if(CTypeAnalyzer.is_boolean(type)) {
			if(StateEvaluation.is_zero_number(parameter)) {
				output.put(graph.get_error_set().set_bool(expression, false), constraints);
			}
			else {
				output.put(graph.get_error_set().set_bool(expression, true), constraints);
			}
		}
		else if(CTypeAnalyzer.is_integer(type)) {
			if(parameter instanceof Long) {
				output.put(graph.get_error_set().set_numb(expression, ((Long) parameter).longValue()), constraints);
			}
			else if(parameter instanceof Double) {
				output.put(graph.get_error_set().set_numb(expression, ((Double) parameter).longValue()), constraints);
			}
		}
		else if(CTypeAnalyzer.is_real(type)) {
			if(parameter instanceof Long) {
				output.put(graph.get_error_set().set_numb(expression, ((Long) parameter).doubleValue()), constraints);
			}
			else if(parameter instanceof Double) {
				output.put(graph.get_error_set().set_numb(expression, ((Double) parameter).doubleValue()), constraints);
			}
		}
		else if(CTypeAnalyzer.is_pointer(type)) {
			if(StateEvaluation.is_zero_number(parameter)) {
				output.put(graph.get_error_set().set_addr(expression, StateError.NullPointer), constraints);
			}
			else {
				output.put(graph.get_error_set().set_addr(expression, StateError.InvalidAddr), constraints);
			}
		}
	}

	@Override
	protected void propagate_neg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = (CirExpression) cir_target;
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		if(CTypeAnalyzer.is_boolean(type)) { return; }
		else if(CTypeAnalyzer.is_number(type)) {
			output.put(graph.get_error_set().neg_numb(expression), constraints);
		}
		else if(CTypeAnalyzer.is_pointer(type)) {
			output.put(graph.get_error_set().set_addr(expression, StateError.InvalidAddr), constraints);
		}
	}

	@Override
	protected void propagate_xor_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = (CirExpression) cir_target;
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		if(CTypeAnalyzer.is_boolean(type)) { 
			SymExpression constraint = StateEvaluation.new_condition(expression, false);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().set_bool(expression, true), constraints);
		}
		else if(CTypeAnalyzer.is_number(type)) {
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
		else if(CTypeAnalyzer.is_pointer(type)) {
			output.put(graph.get_error_set().set_addr(expression, StateError.InvalidAddr), constraints);
		}
	}

	@Override
	protected void propagate_rsv_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = (CirExpression) cir_target;
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		if(CTypeAnalyzer.is_boolean(type)) { 
			SymExpression constraint = StateEvaluation.new_condition(expression, false);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().set_bool(expression, true), constraints);
		}
		else if(CTypeAnalyzer.is_number(type)) {
			output.put(graph.get_error_set().rsv_numb(expression), constraints);
		}
		else if(CTypeAnalyzer.is_pointer(type)) {
			output.put(graph.get_error_set().set_addr(expression, StateError.InvalidAddr), constraints);
		}
	}

	@Override
	protected void propagate_dif_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		Object difference = this.get_number(error.get_operand(1));
		if(StateEvaluation.is_positive_number(difference)) {
			this.propagate_inc_numb(error, cir_target, graph, output);
		}
		else if(StateEvaluation.is_negative_number(difference)) {
			this.propagate_dec_numb(error, cir_target, graph, output);
		}
	}

	@Override
	protected void propagate_inc_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = (CirExpression) cir_target;
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		if(CTypeAnalyzer.is_boolean(type)) { 
			SymExpression constraint = StateEvaluation.new_condition(expression, false);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().set_bool(expression, true), constraints);
		}
		else if(CTypeAnalyzer.is_number(type)) {
			output.put(graph.get_error_set().inc_numb(expression), constraints);
		}
		else if(CTypeAnalyzer.is_pointer(type)) {
			output.put(graph.get_error_set().set_addr(expression, StateError.InvalidAddr), constraints);
		}
	}

	@Override
	protected void propagate_dec_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = (CirExpression) cir_target;
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		if(CTypeAnalyzer.is_boolean(type)) { 
			SymExpression constraint = StateEvaluation.new_condition(expression, false);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().set_bool(expression, true), constraints);
		}
		else if(CTypeAnalyzer.is_number(type)) {
			output.put(graph.get_error_set().dec_numb(expression), constraints);
		}
		else if(CTypeAnalyzer.is_pointer(type)) {
			output.put(graph.get_error_set().set_addr(expression, StateError.InvalidAddr), constraints);
		}
	}

	@Override
	protected void propagate_chg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = (CirExpression) cir_target;
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		if(CTypeAnalyzer.is_boolean(type)) { 
			SymExpression constraint = StateEvaluation.new_condition(expression, false);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().set_bool(expression, true), constraints);
		}
		else if(CTypeAnalyzer.is_number(type)) {
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
		else if(CTypeAnalyzer.is_pointer(type)) {
			output.put(graph.get_error_set().set_addr(expression, StateError.InvalidAddr), constraints);
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
