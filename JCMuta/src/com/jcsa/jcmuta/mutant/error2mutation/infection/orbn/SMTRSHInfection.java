package com.jcsa.jcmuta.mutant.error2mutation.infection.orbn;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.OPRTInfection;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

public class SMTRSHInfection extends OPRTInfection {

	@Override
	protected SymExpression muta_expression(CirExpression expression, CirExpression loperand, CirExpression roperand)
			throws Exception {
		return StateEvaluation.binary_expression(expression.
				get_data_type(), COperator.righ_shift, loperand, roperand);
	}

	@Override
	protected boolean partial_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		Object lconstant = StateEvaluation.get_constant_value(loperand);
		Object rconstant = StateEvaluation.get_constant_value(roperand);
		SymExpression constraint, lcondition, rcondition; StateConstraints constraints;
		CirStatement statement = expression.statement_of();
		
		if(!(lconstant instanceof SymExpression)) {
			if(lconstant instanceof Long) {
				if(((Long) lconstant).longValue() == 0) {
					constraint = StateEvaluation.smaller_tn(loperand, roperand);
					constraints = StateEvaluation.get_conjunctions();
					this.add_constraint(constraints, statement, constraint);
					output.put(graph.get_error_set().set_bool(expression, false), constraints);
				}
				else {
					lcondition = StateEvaluation.greater_eq(roperand, StateInfection.max_bitwise);
					rcondition = StateEvaluation.smaller_tn(loperand, roperand);
					constraints = StateEvaluation.get_conjunctions();
					this.add_constraint(constraints, statement, lcondition);
					this.add_constraint(constraints, statement, rcondition);
					output.put(graph.get_error_set().set_bool(expression, false), constraints);
					
					constraint = StateEvaluation.greater_eq(loperand, roperand);
					constraints = StateEvaluation.get_conjunctions();
					this.add_constraint(constraints, statement, constraint);
					output.put(graph.get_error_set().set_bool(expression, true), constraints);
				}
				return true;
			}
		}
		
		if(!(rconstant instanceof SymExpression)) {
			if(rconstant instanceof Long) {
				if(((Long) rconstant).longValue() >= StateInfection.max_bitwise) {
					constraint = StateEvaluation.smaller_tn(loperand, roperand);
					constraints = StateEvaluation.get_conjunctions();
					this.add_constraint(constraints, statement, constraint);
					output.put(graph.get_error_set().set_bool(expression, false), constraints);
				}
				else {
					lcondition = StateEvaluation.equal_with(loperand, 0L);
					rcondition = StateEvaluation.smaller_tn(loperand, roperand);
					constraints = StateEvaluation.get_conjunctions();
					this.add_constraint(constraints, statement, lcondition);
					this.add_constraint(constraints, statement, rcondition);
					output.put(graph.get_error_set().set_bool(expression, false), constraints);
					
					constraint = StateEvaluation.greater_eq(loperand, roperand);
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
		SymExpression constraint, lcondition, rcondition; StateConstraints constraints;
		CirStatement statement = expression.statement_of();
		
		lcondition = StateEvaluation.logic_or(
				StateEvaluation.equal_with(loperand, 0L), 
				StateEvaluation.greater_eq(roperand, StateInfection.max_bitwise));
		rcondition = StateEvaluation.smaller_tn(loperand, roperand);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, lcondition);
		this.add_constraint(constraints, statement, rcondition);
		output.put(graph.get_error_set().set_bool(expression, false), constraints);
		
		constraint = StateEvaluation.greater_eq(loperand, roperand);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, constraint);
		output.put(graph.get_error_set().set_bool(expression, true), constraints);
		
		return true;
	}

}
