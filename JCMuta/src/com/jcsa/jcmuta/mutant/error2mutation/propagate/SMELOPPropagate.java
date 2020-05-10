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

public class SMELOPPropagate extends StatePropagate {

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
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		Object loperand = this.get_number(error.get_operand(1)); 
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		SymExpression constraint; StateConstraints constraints;
		
		if(!(roperand instanceof SymExpression)) {
			Boolean result = (Boolean) this.smaller_eq(loperand, roperand);
			if(result) {
				constraints = StateEvaluation.get_conjunctions();
				constraint = StateEvaluation.greater_tn(
						expression.get_operand(0), expression.get_operand(1));
				this.add_constraint(constraints, expression.statement_of(), constraint);
				output.put(graph.get_error_set().set_bool(expression, true), constraints);
			}
			else {
				constraints = StateEvaluation.get_conjunctions();
				constraint = StateEvaluation.smaller_eq(
						expression.get_operand(0), expression.get_operand(1));
				this.add_constraint(constraints, expression.statement_of(), constraint);
				output.put(graph.get_error_set().set_bool(expression, false), constraints);
			}
		}
		else {
			constraints = StateEvaluation.get_conjunctions();
			constraint = StateEvaluation.greater_tn(
					expression.get_operand(0), expression.get_operand(1));
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().set_bool(expression, true), constraints);
			
			constraints = StateEvaluation.get_conjunctions();
			constraint = StateEvaluation.smaller_eq(
					expression.get_operand(0), expression.get_operand(1));
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().set_bool(expression, false), constraints);
		}
	}

	@Override
	protected void propagate_neg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_chg_numb(error, cir_target, graph, output);
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
		Long difference = (Long) this.get_number(error.get_operand(1));
		if(difference > 0) {
			this.propagate_inc_numb(error, cir_target, graph, output);
		}
		else if(difference < 0) {
			this.propagate_dec_numb(error, cir_target, graph, output);
		}
	}

	@Override
	protected void propagate_inc_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		SymExpression constraint; StateConstraints constraints;
		constraints = StateEvaluation.get_conjunctions();
		constraint = StateEvaluation.smaller_eq(
				expression.get_operand(0), expression.get_operand(1));
		this.add_constraint(constraints, expression.statement_of(), constraint);
		output.put(graph.get_error_set().set_bool(expression, false), constraints);
	}

	@Override
	protected void propagate_dec_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		SymExpression constraint; StateConstraints constraints;
		constraints = StateEvaluation.get_conjunctions();
		constraint = StateEvaluation.greater_tn(
				expression.get_operand(0), expression.get_operand(1));
		this.add_constraint(constraints, expression.statement_of(), constraint);
		output.put(graph.get_error_set().set_bool(expression, true), constraints);
	}

	@Override
	protected void propagate_chg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		SymExpression constraint; StateConstraints constraints;
		
		constraints = StateEvaluation.get_conjunctions();
		constraint = StateEvaluation.greater_tn(
				expression.get_operand(0), expression.get_operand(1));
		this.add_constraint(constraints, expression.statement_of(), constraint);
		output.put(graph.get_error_set().set_bool(expression, true), constraints);
		
		constraints = StateEvaluation.get_conjunctions();
		constraint = StateEvaluation.smaller_eq(
				expression.get_operand(0), expression.get_operand(1));
		this.add_constraint(constraints, expression.statement_of(), constraint);
		output.put(graph.get_error_set().set_bool(expression, false), constraints);
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
