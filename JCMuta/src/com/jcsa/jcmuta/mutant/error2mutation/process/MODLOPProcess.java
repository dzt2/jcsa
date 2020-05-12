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

public class MODLOPProcess extends StateProcess {

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
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		Boolean loperand = (Boolean) error.get_operand(1);
		
		if(!(roperand instanceof SymExpression)) {
			long rvalue = (Long) this.get_number(roperand);
			if(rvalue == 1 || rvalue == -1) { return; /** equivalent mutants detected **/ }
			else {
				output.put(graph.get_error_set().set_bool(expression, loperand), constraints);
			}
		}
		else {
			this.add_constraint(constraints, expression.statement_of(), 
					StateEvaluation.equal_with(expression.get_operand(1), 1));
			this.add_constraint(constraints, expression.statement_of(), 
					StateEvaluation.equal_with(expression.get_operand(1), -1));
			output.put(graph.get_error_set().set_bool(expression, loperand), constraints);
		}
	}

	@Override
	protected void propagate_chg_bool(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		
		if(!(roperand instanceof SymExpression)) {
			long rvalue = (Long) this.get_number(roperand);
			if(rvalue == 1 || rvalue == -1) { return; /** equivalent mutants detected **/ }
			else {
				output.put(graph.get_error_set().chg_bool(expression), constraints);
			}
		}
		else {
			this.add_constraint(constraints, expression.statement_of(), 
					StateEvaluation.equal_with(expression.get_operand(1), 1));
			this.add_constraint(constraints, expression.statement_of(), 
					StateEvaluation.equal_with(expression.get_operand(1), -1));
			output.put(graph.get_error_set().chg_bool(expression), constraints);
		}
	}

	@Override
	protected void propagate_set_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object loperand = error.get_operand(1);
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		
		/** CASE-1. Constant right operand **/
		if(!(roperand instanceof SymExpression)) {
			Long rvalue = (Long) this.get_number(roperand);
			if(rvalue == 1 || rvalue == -1) { return; /** equivalent mutant detected **/ }
			else {
				Long lvalue = (Long) this.get_number(loperand);
				if(lvalue == 0) {
					output.put(graph.get_error_set().set_numb(expression, 0), constraints);
				}
				else if(lvalue == 1 || lvalue == -1) {
					output.put(graph.get_error_set().set_numb(expression, 1), constraints);
				}
				else {
					Long result = (Long) this.arith_mod(loperand, roperand);
					output.put(graph.get_error_set().set_numb(expression, result), constraints);
				}
			}
		}
		/** CASE-2. Dynamical right operand **/
		else {
			Long lvalue = (Long) this.get_number(loperand);
			this.add_constraint(constraints, expression.statement_of(), 
					StateEvaluation.equal_with(expression.get_operand(1), 1));
			this.add_constraint(constraints, expression.statement_of(), 
					StateEvaluation.equal_with(expression.get_operand(1), -1));
			if(lvalue == 0) {
				output.put(graph.get_error_set().set_numb(expression, 0), constraints);
			}
			else if(lvalue == 1 || lvalue == -1) {
				output.put(graph.get_error_set().set_numb(expression, 1), constraints);
			}
			else {
				Long result = (Long) this.arith_mod(loperand, roperand);
				output.put(graph.get_error_set().set_numb(expression, result), constraints);
			}
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
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object loperand = error.get_operand(0);
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		
		if(!(roperand instanceof SymExpression)) {
			long difference = (Long) this.arith_mod(loperand, roperand);
			if(difference == 0) { return; /** equivalent mutant detect **/ }
			else {
				output.put(graph.get_error_set().dif_numb(expression, difference), constraints);
			}
		}
		else {
			SymExpression constraint = StateEvaluation.not_equals(
					StateEvaluation.get_symbol(expression.get_operand(0)), 
					StateEvaluation.multiply_expression(expression.
							get_data_type(), expression.get_operand(1)));
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
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
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		
		if(!(roperand instanceof SymExpression)) {
			long rvalue = (Long) this.get_number(roperand);
			if(rvalue == 1 || rvalue == -1) { return; /** equivalent mutant **/ }
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
		else {
			SymExpression constraint = StateEvaluation.not_equals(
					StateEvaluation.get_symbol(expression.get_operand(0)), 
					StateEvaluation.multiply_expression(expression.
							get_data_type(), expression.get_operand(1)));
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
		this.propagate_chg_numb(error, cir_target, graph, output);
	}

	@Override
	protected void propagate_mut_refer(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		this.propagate_mut_expr(error, cir_target, graph, output);
	}

}
