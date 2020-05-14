package com.jcsa.jcmuta.mutant.error2mutation.process;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateProcess;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

public class NOTProcess extends StateProcess {

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
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object parameter = this.get_number(error.get_operand(1));
		SymExpression constraint;
		
		if(StateEvaluation.is_zero_number(parameter)) {
			constraint = StateEvaluation.new_condition(expression, false);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().set_bool(expression, true), constraints);
		}
		else {
			constraint = StateEvaluation.new_condition(expression, true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().set_bool(expression, false), constraints);
		}
	}

	@Override
	protected void propagate_chg_bool(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = (CirExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		output.put(graph.get_error_set().chg_bool(expression), constraints);
	}

	@Override
	protected void propagate_set_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_set_bool(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_neg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
	}

	@Override
	protected void propagate_xor_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = (CirExpression) cir_target;
		CirExpression operand = (CirExpression) error.get_operand(1);
		Long difference = (Long) error.get_operand(1); 
		StateConstraints constraints; SymExpression constraint;
		
		constraint = StateEvaluation.equal_with(operand, difference.longValue());
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), constraint);
		output.put(graph.get_error_set().set_bool(expression, true), constraints);
		
		constraint = StateEvaluation.not_equals(operand, difference.longValue());
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), constraint);
		output.put(graph.get_error_set().set_bool(expression, false), constraints);
	}

	@Override
	protected void propagate_rsv_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = (CirExpression) cir_target;
		CirExpression operand = (CirExpression) error.get_operand(0);
		StateConstraints constraints; SymExpression constraint;
		
		constraint = StateEvaluation.equal_with(operand, -1L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), constraint);
		output.put(graph.get_error_set().set_bool(expression, true), constraints);
		
		constraint = StateEvaluation.not_equals(operand, -1L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), constraint);
		output.put(graph.get_error_set().set_bool(expression, false), constraints);
		
	}

	@Override
	protected void propagate_dif_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirExpression expression = (CirExpression) cir_target;
		CirExpression operand = (CirExpression) error.get_operand(0);
		Object difference = this.get_number(error.get_operand(1)); 
		StateConstraints constraints; SymExpression constraint;
		
		if(difference instanceof Long) {
			constraint = StateEvaluation.equal_with(operand,-((Long) difference).longValue());
			constraints = StateEvaluation.get_conjunctions();
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().set_bool(expression, true), constraints);
			
			constraint = StateEvaluation.not_equals(operand,-((Long) difference).longValue());
			constraints = StateEvaluation.get_conjunctions();
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().set_bool(expression, false), constraints);
		}
		else if(difference instanceof Double) {
			constraint = StateEvaluation.equal_with(operand,-((Double) difference).doubleValue());
			constraints = StateEvaluation.get_conjunctions();
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().set_bool(expression, true), constraints);
			
			constraint = StateEvaluation.not_equals(operand,-((Double) difference).doubleValue());
			constraints = StateEvaluation.get_conjunctions();
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().set_bool(expression, false), constraints);
		}
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
		CirExpression expression = (CirExpression) cir_target;
		CirExpression operand = (CirExpression) error.get_operand(0);
		StateConstraints constraints; SymExpression constraint;
		
		constraint = StateEvaluation.equal_with(operand, 0L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), constraint);
		output.put(graph.get_error_set().set_bool(expression, true), constraints);
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
