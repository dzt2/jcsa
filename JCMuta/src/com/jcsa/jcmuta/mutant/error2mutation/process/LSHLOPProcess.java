package com.jcsa.jcmuta.mutant.error2mutation.process;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcmuta.mutant.error2mutation.StateProcess;
import com.jcsa.jcparse.lang.irlang.CirNode;
import com.jcsa.jcparse.lang.irlang.expr.CirComputeExpression;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

public class LSHLOPProcess extends StateProcess {

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
		
		if(!(roperand instanceof SymExpression)) {
			long rvalue = ((Long) this.get_number(roperand)).longValue();
			if(rvalue >= StateInfection.max_bitwise) { return; /** unable to propagate **/ }
			else {
				Long result = (Long) this.bitws_lsh(loperand, roperand);
				output.put(graph.get_error_set().set_numb(expression, result.longValue()), constraints);
			}
		}
		else {
			SymExpression constraint = StateEvaluation.
					smaller_tn(expression.get_operand(1), StateInfection.max_bitwise);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			
			if(loperand.booleanValue()) {
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
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		if(!(roperand instanceof SymExpression)) {
			long rvalue = ((Long) this.get_number(roperand)).longValue();
			if(rvalue >= StateInfection.max_bitwise) { return; /** unable to propagate **/ }
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
		else {
			SymExpression constraint = StateEvaluation.
					smaller_tn(expression.get_operand(1), StateInfection.max_bitwise);
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
		
		if(!(roperand instanceof SymExpression)) {
			long rvalue = ((Long) this.get_number(roperand)).longValue();
			if(rvalue >= StateInfection.max_bitwise) { return; /** unable to propagate **/ }
			else {
				Long result = (Long) this.bitws_lsh(loperand, roperand);
				output.put(graph.get_error_set().set_numb(expression, result.longValue()), constraints);
			}
		}
		else {
			SymExpression constraint = StateEvaluation.
					smaller_tn(expression.get_operand(1), StateInfection.max_bitwise);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			long lvalue = ((Long) this.get_number(loperand)).longValue();
			
			if(lvalue == 0) {
				output.put(graph.get_error_set().set_numb(expression, 0L), constraints);
			}
			else { output.put(graph.get_error_set().chg_numb(expression), constraints); }
		}
	}

	@Override
	protected void propagate_neg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		if(!(roperand instanceof SymExpression)) {
			long rvalue = ((Long) this.get_number(roperand)).longValue();
			if(rvalue >= StateInfection.max_bitwise) { return; /** unable to propagate **/ }
			else if(rvalue == 0) { 
				output.put(graph.get_error_set().neg_numb(expression), constraints); 
			}
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints); 
			}
		}
		else {
			SymExpression constraint = StateEvaluation.
					smaller_tn(expression.get_operand(1), StateInfection.max_bitwise);
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
		
		if(!(roperand instanceof SymExpression)) {
			long rvalue = ((Long) this.get_number(roperand)).longValue();
			if(rvalue >= StateInfection.max_bitwise) { return; /** unable to propagate **/ }
			else if(rvalue == 0) { 
				Long lvalue = (Long) this.get_number(error.get_operand(1));
				output.put(graph.get_error_set().xor_numb(expression, lvalue), constraints); 
			}
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints); 
			}
		}
		else {
			SymExpression constraint = StateEvaluation.
					smaller_tn(expression.get_operand(1), StateInfection.max_bitwise);
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
		
		if(!(roperand instanceof SymExpression)) {
			long rvalue = ((Long) this.get_number(roperand)).longValue();
			if(rvalue >= StateInfection.max_bitwise) { return; /** unable to propagate **/ }
			else if(rvalue == 0) { 
				output.put(graph.get_error_set().rsv_numb(expression), constraints); 
			}
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints); 
			}
		}
		else {
			SymExpression constraint = StateEvaluation.
					smaller_tn(expression.get_operand(1), StateInfection.max_bitwise);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_dif_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		Object loperand = this.get_number(error.get_operand(1));
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(1));
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		
		if(!(roperand instanceof SymExpression)) {
			long rvalue = ((Long) this.get_number(roperand)).longValue();
			if(rvalue >= StateInfection.max_bitwise) { return; /** unable to propagate **/ }
			else {
				Long result = (Long) this.bitws_lsh(loperand, roperand);
				if(result.longValue() != 0L) {
					output.put(graph.get_error_set().dif_numb(expression, rvalue), constraints);
				}
			}
		}
		else {
			SymExpression constraint = StateEvaluation.
					smaller_tn(expression.get_operand(1), StateInfection.max_bitwise);
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
		
		if(!(roperand instanceof SymExpression)) {
			long rvalue = ((Long) this.get_number(roperand)).longValue();
			if(rvalue >= StateInfection.max_bitwise) { return; /** unable to propagate **/ }
			else if(rvalue == 0) { 
				output.put(graph.get_error_set().inc_numb(expression), constraints); 
			}
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints); 
			}
		}
		else {
			SymExpression constraint = StateEvaluation.
					smaller_tn(expression.get_operand(1), StateInfection.max_bitwise);
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
		
		if(!(roperand instanceof SymExpression)) {
			long rvalue = ((Long) this.get_number(roperand)).longValue();
			if(rvalue >= StateInfection.max_bitwise) { return; /** unable to propagate **/ }
			else if(rvalue == 0) { 
				output.put(graph.get_error_set().dec_numb(expression), constraints); 
			}
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints); 
			}
		}
		else {
			SymExpression constraint = StateEvaluation.
					smaller_tn(expression.get_operand(1), StateInfection.max_bitwise);
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
		
		if(!(roperand instanceof SymExpression)) {
			long rvalue = ((Long) this.get_number(roperand)).longValue();
			if(rvalue >= StateInfection.max_bitwise) { return; /** unable to propagate **/ }
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints); 
			}
		}
		else {
			SymExpression constraint = StateEvaluation.
					smaller_tn(expression.get_operand(1), StateInfection.max_bitwise);
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
