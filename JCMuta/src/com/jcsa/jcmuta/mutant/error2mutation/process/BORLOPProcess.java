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

public class BORLOPProcess extends StateProcess {

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
		Boolean loperand = (Boolean) error.get_operand(1);
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		/** partial evaluation **/
		if(!(roperand instanceof SymExpression)) {
			Long rvalue = (Long) this.get_number(roperand);
			if(rvalue.longValue() == -1L) { return; /** equivalent mutant detected **/ }
			else {
				long result = ((Long) this.bitws_ior(loperand, roperand)).longValue();
				output.put(graph.get_error_set().set_numb(expression, result), constraints);
			}
		}
		else {
			SymExpression constraint = StateEvaluation.not_equals(expression.get_operand(1), -1L);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			if(loperand) {
				output.put(graph.get_error_set().dif_numb(expression, 1L), constraints);
			}
			else {
				output.put(graph.get_error_set().dif_numb(expression, -1L), constraints);
			}
		}
	}

	@Override
	protected void propagate_chg_bool(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		/** partial evaluation **/
		if(!(roperand instanceof SymExpression)) {
			Long rvalue = (Long) this.get_number(roperand);
			if(rvalue.longValue() == -1L) { return; /** equivalence **/ }
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
		else {
			SymExpression constraint = StateEvaluation.not_equals(expression.get_operand(1), -1L);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_set_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		Object loperand = error.get_operand(1);
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		/** partial evaluation **/
		if(!(roperand instanceof SymExpression)) {
			Long rvalue = (Long) this.get_number(roperand);
			if(rvalue.longValue() == -1L) { return; /** equivalent mutant detected **/ }
			else {
				long result = ((Long) this.bitws_ior(loperand, roperand)).longValue();
				output.put(graph.get_error_set().set_numb(expression, result), constraints);
			}
		}
		/** dynamic evaluation **/
		else {
			SymExpression constraint = StateEvaluation.not_equals(expression.get_operand(1), -1L);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			Long lvalue = (Long) this.get_number(loperand);
			if(lvalue.longValue() == -1L) {
				output.put(graph.get_error_set().set_numb(expression, -1L), constraints);
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
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		/** partial evaluation **/
		if(!(roperand instanceof SymExpression)) {
			Long rvalue = (Long) this.get_number(roperand);
			if(rvalue.longValue() == -1L) { return; /** equivalent mutant detected **/ }
			else if(rvalue.longValue() == 0L) {
				output.put(graph.get_error_set().neg_numb(expression), constraints);
			}
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
		/** dynamic evaluation **/
		else {
			SymExpression constraint = StateEvaluation.not_equals(expression.get_operand(1), -1L);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_xor_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		/** partial evaluation **/
		if(!(roperand instanceof SymExpression)) {
			Long rvalue = (Long) this.get_number(roperand);
			if(rvalue.longValue() == -1L) { return; /** equivalent mutant detected **/ }
			else if(rvalue.longValue() == 0L) {
				Long loperand = (Long) error.get_operand(1);
				output.put(graph.get_error_set().
						xor_numb(expression, loperand.longValue()), constraints);
			}
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
		/** dynamic evaluation **/
		else {
			SymExpression constraint = StateEvaluation.not_equals(expression.get_operand(1), -1L);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_rsv_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		/** partial evaluation **/
		if(!(roperand instanceof SymExpression)) {
			Long rvalue = (Long) this.get_number(roperand);
			if(rvalue.longValue() == -1L) { return; /** equivalent mutant detected **/ }
			else if(rvalue.longValue() == 0L) {
				output.put(graph.get_error_set().rsv_numb(expression), constraints);
			}
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
		/** dynamic evaluation **/
		else {
			SymExpression constraint = StateEvaluation.not_equals(expression.get_operand(1), -1L);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_dif_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		/** partial evaluation **/
		if(!(roperand instanceof SymExpression)) {
			Long rvalue = (Long) this.get_number(roperand);
			if(rvalue.longValue() == -1L) { return; /** equivalent mutant detected **/ }
			else if(rvalue.longValue() == 0L) {
				long difference = ((Long) error.get_operand(1)).longValue();
				output.put(graph.get_error_set().dif_numb(expression, difference), constraints);
			}
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
		/** dynamic evaluation **/
		else {
			SymExpression constraint = StateEvaluation.not_equals(expression.get_operand(1), -1L);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_inc_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		/** partial evaluation **/
		if(!(roperand instanceof SymExpression)) {
			Long rvalue = (Long) this.get_number(roperand);
			if(rvalue.longValue() == -1L) { return; /** equivalent mutant detected **/ }
			else if(rvalue.longValue() == 0L) {
				output.put(graph.get_error_set().inc_numb(expression), constraints);
			}
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
		/** dynamic evaluation **/
		else {
			SymExpression constraint = StateEvaluation.not_equals(expression.get_operand(1), -1L);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_dec_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		/** partial evaluation **/
		if(!(roperand instanceof SymExpression)) {
			Long rvalue = (Long) this.get_number(roperand);
			if(rvalue.longValue() == -1L) { return; /** equivalent mutant detected **/ }
			else if(rvalue.longValue() == 0L) {
				output.put(graph.get_error_set().dec_numb(expression), constraints);
			}
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
		/** dynamic evaluation **/
		else {
			SymExpression constraint = StateEvaluation.not_equals(expression.get_operand(1), -1L);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_chg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		/** partial evaluation **/
		if(!(roperand instanceof SymExpression)) {
			Long rvalue = (Long) this.get_number(roperand);
			if(rvalue.longValue() == -1L) { return; /** equivalent mutant detected **/ }
			else if(rvalue.longValue() == 0L) {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
		/** dynamic evaluation **/
		else {
			SymExpression constraint = StateEvaluation.not_equals(expression.get_operand(1), -1L);
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
		this.propagate_set_numb(error, cir_target, graph, output);
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
