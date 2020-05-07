package com.jcsa.jcmuta.mutant.error2mutation.infection.obrn;

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

public class LSHGREInfection extends OPRTInfection {

	@Override
	protected SymExpression muta_expression(CirExpression expression, CirExpression loperand, CirExpression roperand)
			throws Exception {
		return StateEvaluation.greater_eq(loperand, roperand);
	}

	@Override
	protected boolean partial_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		Object lconstant = StateEvaluation.get_constant_value(loperand);
		Object rconstant = StateEvaluation.get_constant_value(roperand);
		SymExpression constraint; StateConstraints constraints;
		CirStatement statement = expression.statement_of();
		
		if(!(lconstant instanceof SymExpression)) {
			if(lconstant instanceof Long) {
				if(((Long) lconstant).longValue() == 0) {
					constraint = StateEvaluation.greater_eq(loperand, roperand);
					constraints = StateEvaluation.get_conjunctions();
					this.add_constraint(constraints, statement, constraint);
					output.put(graph.get_error_set().dif_numb(expression, 1L), constraints);
				}
				else {
					constraint = StateEvaluation.greater_eq(loperand, roperand);
					constraints = StateEvaluation.get_conjunctions();
					this.add_constraint(constraints, statement, constraint);
					output.put(graph.get_error_set().set_bool(expression, true), constraints);
					
					constraint = StateEvaluation.smaller_tn(loperand, roperand);
					constraints = StateEvaluation.get_conjunctions();
					this.add_constraint(constraints, statement, constraint);
					output.put(graph.get_error_set().set_bool(expression, false), constraints);
					
					constraints = StateEvaluation.get_disjunctions();
					this.add_constraint(constraints, statement, StateEvaluation.logic_and(
							StateEvaluation.greater_eq(loperand, roperand), 
							StateEvaluation.greater_eq(roperand, StateInfection.max_bitwise)));
					output.put(graph.get_error_set().dif_numb(expression, 1L), constraints);
				}
				return true;
			}
		}
		
		if(!(rconstant instanceof SymExpression)) {
			if(rconstant instanceof Long) {
				if(((Long) rconstant).longValue() >= StateInfection.max_bitwise) {
					constraint = StateEvaluation.greater_eq(loperand, roperand);
					constraints = StateEvaluation.get_conjunctions();
					this.add_constraint(constraints, statement, constraint);
					output.put(graph.get_error_set().dif_numb(expression, 1L), constraints);
				}
				else {
					constraint = StateEvaluation.greater_eq(loperand, roperand);
					constraints = StateEvaluation.get_conjunctions();
					this.add_constraint(constraints, statement, constraint);
					output.put(graph.get_error_set().set_bool(expression, true), constraints);
					
					constraint = StateEvaluation.smaller_tn(loperand, roperand);
					constraints = StateEvaluation.get_conjunctions();
					this.add_constraint(constraints, statement, constraint);
					output.put(graph.get_error_set().set_bool(expression, false), constraints);
					
					constraints = StateEvaluation.get_disjunctions();
					this.add_constraint(constraints, statement, StateEvaluation.logic_and(
							StateEvaluation.greater_eq(loperand, roperand), 
							StateEvaluation.equal_with(loperand, 0L)));
					output.put(graph.get_error_set().dif_numb(expression, 1L), constraints);
				}
				return true;
			}
		}
		
		return false;
	}

	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression constraint; StateConstraints constraints;
		CirStatement statement = expression.statement_of();
		
		constraint = StateEvaluation.greater_eq(loperand, roperand);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, constraint);
		output.put(graph.get_error_set().set_bool(expression, true), constraints);
		
		constraint = StateEvaluation.smaller_tn(loperand, roperand);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, constraint);
		output.put(graph.get_error_set().set_bool(expression, false), constraints);
		
		constraints = StateEvaluation.get_disjunctions();
		this.add_constraint(constraints, statement, StateEvaluation.logic_and(
				StateEvaluation.greater_eq(loperand, roperand), 
				StateEvaluation.equal_with(loperand, 0L)));
		this.add_constraint(constraints, statement, StateEvaluation.logic_and(
				StateEvaluation.greater_eq(loperand, roperand), 
				StateEvaluation.greater_eq(roperand, StateInfection.max_bitwise)));
		output.put(graph.get_error_set().dif_numb(expression, 1L), constraints);
		
		return true;
	}

}
