package com.jcsa.jcmuta.mutant.error2mutation.infection.obln;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.OPRTInfection;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

/**
 * loperand == 0						--> equivalence
 * loperand != 0 and roperand >= max	--> dif_numb(1)
 * loperand != 0 and roperand == 0		--> set_false
 * loperand != 0 and roperand != 0		--> set_true
 * @author yukimula
 *
 */
public class LSHLANInfection extends OPRTInfection {

	@Override
	protected SymExpression muta_expression(CirExpression expression, CirExpression loperand, CirExpression roperand)
			throws Exception {
		return StateEvaluation.logic_and(loperand, roperand);
	}

	@Override
	protected boolean partial_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		Object lconstant = StateEvaluation.get_constant_value(loperand);
		Object rconstant = StateEvaluation.get_constant_value(roperand);
		CirStatement statement = expression.statement_of();
		SymExpression constraint; StateConstraints constraints;
		
		if(!(lconstant instanceof SymExpression)) {
			if(lconstant instanceof Long) {
				if(((Long) lconstant).longValue() == 0L) {
					/** equivalent mutant detected **/
				}
				else {
					constraint = StateEvaluation.equal_with(roperand, 0L);
					constraints = StateEvaluation.get_conjunctions();
					this.add_constraint(constraints, statement, constraint);
					output.put(graph.get_error_set().set_bool(expression, false), constraints);
					
					constraint = StateEvaluation.not_equals(roperand, 0L);
					constraints = StateEvaluation.get_conjunctions();
					this.add_constraint(constraints, statement, constraint);
					output.put(graph.get_error_set().set_bool(expression, true), constraints);
					
					constraint = StateEvaluation.greater_eq(roperand, StateInfection.max_bitwise);
					constraints = StateEvaluation.get_conjunctions();
					this.add_constraint(constraints, statement, constraint);
					output.put(graph.get_error_set().dif_numb(expression, 1L), constraints);
				}
				return true;
			}
		}
		
		if(!(rconstant instanceof SymExpression)) {
			if(rconstant instanceof Long) {
				if(((Long) rconstant).longValue() == 0) {
					constraint = StateEvaluation.not_equals(loperand, 0L);
					constraints = StateEvaluation.get_conjunctions();
					this.add_constraint(constraints, statement, constraint);
					output.put(graph.get_error_set().set_bool(expression, false), constraints);
				}
				else if(((Long) rconstant).longValue() >= StateInfection.max_bitwise) {
					constraint = StateEvaluation.not_equals(loperand, 0L);
					constraints = StateEvaluation.get_conjunctions();
					this.add_constraint(constraints, statement, constraint);
					output.put(graph.get_error_set().dif_numb(expression, 1L), constraints);
				}
				else {
					constraint = StateEvaluation.not_equals(loperand, 0L);
					constraints = StateEvaluation.get_conjunctions();
					this.add_constraint(constraints, statement, constraint);
					output.put(graph.get_error_set().set_bool(expression, true), constraints);
				}
				return true;
			}
		}
		
		return false;
	}

	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lcondition, rcondition; StateConstraints constraints;
		CirStatement statement = expression.statement_of();
		
		/** loperand != 0 and roperand == 0 --> set_false **/
		lcondition = StateEvaluation.not_equals(loperand, 0L);
		rcondition = StateEvaluation.equal_with(roperand, 0L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, lcondition);
		this.add_constraint(constraints, statement, rcondition);
		output.put(graph.get_error_set().set_bool(expression, false), constraints);
		
		/** loperand != 0 and roperand != 0 --> set_true **/
		rcondition = StateEvaluation.not_equals(roperand, 0L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, lcondition);
		this.add_constraint(constraints, statement, rcondition);
		output.put(graph.get_error_set().set_bool(expression, true), constraints);
		
		/** loperand != 0 and roperand >= max_bitwse --> dif_numb(1) **/
		rcondition = StateEvaluation.greater_eq(roperand, StateInfection.max_bitwise);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, lcondition);
		this.add_constraint(constraints, statement, rcondition);
		output.put(graph.get_error_set().dif_numb(expression, 1L), constraints);
		
		return true;
	}

}
