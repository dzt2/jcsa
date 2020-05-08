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

public class MULROPPropagate extends StatePropagate {

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
		/** constraint as roperand != 0 **/
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		SymExpression constraint = 
				StateEvaluation.new_condition(expression.get_operand(0), true);
		this.add_constraint(constraints, expression.statement_of(), constraint);
		
		/** get the parameter and operand used as following **/
		boolean parameter = ((Boolean) error.get_operand(1)).booleanValue();
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		/** right operand as constant **/
		if(!(roperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(roperand)) {
				return;		/** equivalent mutant detected **/
			}
			else {
				Object rvalue = this.get_number(roperand);
				if(parameter) {
					if(rvalue instanceof Long) {
						output.put(graph.get_error_set().set_numb(expression, ((Long) rvalue).longValue()), 
								StateEvaluation.get_conjunctions());
					}
					else {
						output.put(graph.get_error_set().set_numb(expression, ((Double) rvalue).doubleValue()), 
								StateEvaluation.get_conjunctions());
					}
				}
				else {
					output.put(graph.get_error_set().set_numb(expression, 0L), StateEvaluation.get_conjunctions());
				}
			}
		}
		/** undecidable case with constraint of roperand != 0 **/
		else {
			if(parameter) {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
			else {
				output.put(graph.get_error_set().set_numb(expression, 0), constraints);
			}
		}
	}

	@Override
	protected void propagate_chg_bool(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/** constraint as roperand != 0 **/
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		SymExpression constraint = 
				StateEvaluation.new_condition(expression.get_operand(0), true);
		this.add_constraint(constraints, expression.statement_of(), constraint);
		
		/** get the parameter and operand used as following **/
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		/** right operand as constant **/
		if(!(roperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(roperand)) { return; /** equivalence **/ }
			else {
				output.put(graph.get_error_set().chg_numb(expression), StateEvaluation.get_conjunctions());
			}
		}
		else {
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_set_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/** declarations **/
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		SymExpression constraint = 
				StateEvaluation.new_condition(expression.get_operand(0), true);
		this.add_constraint(constraints, expression.statement_of(), constraint);
		
		/** get parameters from error and expression **/
		Object loperand = error.get_operand(1);
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		/** CASE-1. right operand is constant **/
		if(!(roperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(roperand)) { return; /** equivalence **/ }
			/** set_numb(loperand * roperand) **/
			else {
				Object result = this.arith_mul(loperand, roperand);
				if(result instanceof Long) {
					output.put(graph.get_error_set().set_numb(expression, ((Long) result).longValue()), 
							StateEvaluation.get_conjunctions());
				}
				else {
					output.put(graph.get_error_set().set_numb(expression, ((Double) result).doubleValue()), 
							StateEvaluation.get_conjunctions());
				}
			}
		}
		/** CASE-2. right-operand is dynamical **/
		else {
			/** set_numb(0) **/
			if(StateEvaluation.is_zero_number(loperand)) {
				output.put(graph.get_error_set().set_numb(expression, 0L), constraints);
			}
			/** chg_numb(expr) **/
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
	}

	@Override
	protected void propagate_neg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/** declarations **/
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		/** CASE-1. constant right operand **/
		if(!(roperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(roperand)) { return; /** equivalent **/ }
			else {
				output.put(graph.get_error_set().neg_numb(expression), constraints);
			}
		}
		/** CASE-2. neg_numb with constraints **/
		else {
			SymExpression constraint = 
					StateEvaluation.new_condition(expression.get_operand(0), true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().neg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_xor_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/** declarations **/
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		/** CASE-1. constant right operand **/
		if(!(roperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(roperand)) { return; /** equivalent **/ }
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
		/** CASE-2. neg_numb with constraints **/
		else {
			SymExpression constraint = 
					StateEvaluation.new_condition(expression.get_operand(0), true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_rsv_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/** declarations **/
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		/** CASE-1. constant right operand **/
		if(!(roperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(roperand)) { return; /** equivalent **/ }
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
		/** CASE-2. neg_numb with constraints **/
		else {
			SymExpression constraint = 
					StateEvaluation.new_condition(expression.get_operand(0), true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_dif_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/** declarations **/
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object loperand = error.get_operand(1);
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		/** CASE-1. right-operand is constant **/
		if(!(roperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(roperand)) { return; /** equivalent **/ }
			else {
				Object difference = this.arith_mul(loperand, roperand);
				if(difference instanceof Long) {
					output.put(graph.get_error_set().dif_numb(expression, ((Long) difference).longValue()), constraints);
				}
				else {
					output.put(graph.get_error_set().dif_numb(expression, ((Double) difference).doubleValue()), constraints);
				}
			}
		}
		/** CASE-2. right-operand is undecidable **/
		else {
			SymExpression constraint = 
					StateEvaluation.new_condition(expression.get_operand(0), true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_inc_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/** declarations **/
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		/** CASE-1. right-operand is constant **/
		if(!(roperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(roperand)) { return; /** equivalent **/ }
			else if(StateEvaluation.is_positive_number(roperand)) {
				output.put(graph.get_error_set().inc_numb(expression), constraints);
			}
			else {
				output.put(graph.get_error_set().dec_numb(expression), constraints);
			}
		}
		/** CASE-2. right-operand is undecidable **/
		else {
			SymExpression constraint = 
					StateEvaluation.new_condition(expression.get_operand(0), true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_dec_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/** declarations **/
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		/** CASE-1. right-operand is constant **/
		if(!(roperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(roperand)) { return; /** equivalent **/ }
			else if(StateEvaluation.is_positive_number(roperand)) {
				output.put(graph.get_error_set().dec_numb(expression), constraints);
			}
			else {
				output.put(graph.get_error_set().inc_numb(expression), constraints);
			}
		}
		/** CASE-2. right-operand is undecidable **/
		else {
			SymExpression constraint = 
					StateEvaluation.new_condition(expression.get_operand(0), true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_chg_numb(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/** declarations **/
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		/** CASE-1. right-operand is constant **/
		if(!(roperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(roperand)) { return; /** equivalent **/ }
			else {
				output.put(graph.get_error_set().chg_numb(expression), constraints);
			}
		}
		/** CASE-2. right-operand is undecidable **/
		else {
			SymExpression constraint = 
					StateEvaluation.new_condition(expression.get_operand(0), true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_numb(expression), constraints);
		}
	}

	@Override
	protected void propagate_dif_addr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/** declarations **/
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object loperand = error.get_operand(1);
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		/** CASE-1. right-operand is constant **/
		if(!(roperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(roperand)) { return; /** equivalent **/ }
			else {
				Long difference = (Long) this.arith_mul(loperand, roperand);
				if(difference.longValue() != 0) {
					output.put(graph.get_error_set().dif_addr(
							expression, difference.longValue()), constraints);
				}
			}
		}
		/** CASE-2. right-operand is undecidable **/
		else {
			SymExpression constraint = 
					StateEvaluation.new_condition(expression.get_operand(0), true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_addr(expression), constraints);
		}
	}

	@Override
	protected void propagate_set_addr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/** declarations **/
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object loperand = error.get_operand(1);
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		/** CASE-1. right operand is constant **/
		if(!(roperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(roperand)) { return; /** equivalence **/ }
			/** set_numb(loperand * roperand) **/
			else {
				Long result = (Long) this.arith_mul(loperand, roperand);
				if(result.longValue() == 0L) {
					output.put(graph.get_error_set().set_addr(expression, StateError.NullPointer), constraints);
				}
				else {
					output.put(graph.get_error_set().set_addr(expression, StateError.InvalidAddr), constraints);
				}
			}
		}
		/** CASE-2. right-operand is dynamical **/
		else {
			SymExpression constraint = 
					StateEvaluation.new_condition(expression.get_operand(0), true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_addr(expression), constraints);
		}
	}

	@Override
	protected void propagate_chg_addr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/** declarations **/
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		/** CASE-1. right-operand is constant **/
		if(!(roperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(roperand)) { return; /** equivalent **/ }
			else {
				output.put(graph.get_error_set().chg_addr(expression), constraints);
			}
		}
		/** CASE-2. right-operand is undecidable **/
		else {
			SymExpression constraint = 
					StateEvaluation.new_condition(expression.get_operand(0), true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_addr(expression), constraints);
		}
	}

	@Override
	protected void propagate_mut_expr(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/** declarations **/
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		/** CASE-1. right-operand is constant **/
		if(!(roperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(roperand)) { return; /** equivalent **/ }
			else {
				output.put(graph.get_error_set().chg_addr(expression), constraints);
			}
		}
		/** CASE-2. right-operand is undecidable **/
		else {
			SymExpression constraint = 
					StateEvaluation.new_condition(expression.get_operand(0), true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_addr(expression), constraints);
		}
	}

	@Override
	protected void propagate_mut_refer(StateError error, CirNode cir_target, StateErrorGraph graph,
			Map<StateError, StateConstraints> output) throws Exception {
		/** declarations **/
		CirComputeExpression expression = (CirComputeExpression) cir_target;
		StateConstraints constraints = StateEvaluation.get_conjunctions();
		Object roperand = StateEvaluation.get_constant_value(expression.get_operand(0));
		
		/** CASE-1. right-operand is constant **/
		if(!(roperand instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(roperand)) { return; /** equivalent **/ }
			else {
				output.put(graph.get_error_set().chg_addr(expression), constraints);
			}
		}
		/** CASE-2. right-operand is undecidable **/
		else {
			SymExpression constraint = 
					StateEvaluation.new_condition(expression.get_operand(0), true);
			this.add_constraint(constraints, expression.statement_of(), constraint);
			output.put(graph.get_error_set().chg_addr(expression), constraints);
		}
	}

}
