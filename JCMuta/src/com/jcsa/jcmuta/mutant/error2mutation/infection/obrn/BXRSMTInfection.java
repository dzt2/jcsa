package com.jcsa.jcmuta.mutant.error2mutation.infection.obrn;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.infection.OPRTInfection;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

public class BXRSMTInfection extends OPRTInfection {

	@Override
	protected SymExpression muta_expression(CirExpression expression, CirExpression loperand, CirExpression roperand)
			throws Exception {
		return StateEvaluation.smaller_tn(loperand, roperand);
	}

	private void build(CirExpression expression, CirExpression loperand, long roperand, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression constraint; StateConstraints constraints;
		CirStatement statement = expression.statement_of();
		
		constraint = StateEvaluation.smaller_tn(loperand, roperand);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, constraint);
		output.put(graph.get_error_set().set_bool(expression, true), constraints);
		
		constraint = StateEvaluation.greater_tn(loperand, roperand);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, constraint);
		output.put(graph.get_error_set().set_bool(expression, false), constraints);
	}
	
	private void build(CirExpression expression, CirExpression loperand, double roperand, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression constraint; StateConstraints constraints;
		CirStatement statement = expression.statement_of();
		
		constraint = StateEvaluation.smaller_tn(loperand, roperand);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, constraint);
		output.put(graph.get_error_set().set_bool(expression, true), constraints);
		
		constraint = StateEvaluation.greater_tn(loperand, roperand);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, constraint);
		output.put(graph.get_error_set().set_bool(expression, false), constraints);
	}
	
	private void build(CirExpression expression, long loperand, CirExpression roperand, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression constraint; StateConstraints constraints;
		CirStatement statement = expression.statement_of();
		
		constraint = StateEvaluation.greater_tn(roperand, loperand);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, constraint);
		output.put(graph.get_error_set().set_bool(expression, true), constraints);
		
		constraint = StateEvaluation.smaller_tn(roperand, loperand);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, constraint);
		output.put(graph.get_error_set().set_bool(expression, false), constraints);
	}
	
	private void build(CirExpression expression, double loperand, CirExpression roperand, 
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression constraint; StateConstraints constraints;
		CirStatement statement = expression.statement_of();
		
		constraint = StateEvaluation.greater_tn(roperand, loperand);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, constraint);
		output.put(graph.get_error_set().set_bool(expression, true), constraints);
		
		constraint = StateEvaluation.smaller_tn(roperand, loperand);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, constraint);
		output.put(graph.get_error_set().set_bool(expression, false), constraints);
	}
	
	@Override
	protected boolean partial_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		Object lconstant = StateEvaluation.get_constant_value(loperand);
		Object rconstant = StateEvaluation.get_constant_value(roperand);
		
		if(!(lconstant instanceof SymExpression)) {
			if(lconstant instanceof Boolean) {
				long value = ((Boolean) lconstant).booleanValue() ? 1 : 0;
				this.build(expression, value, roperand, graph, output);
				return true;
			}
			else if(lconstant instanceof Long) {
				long value = ((Long) lconstant).longValue();
				this.build(expression, value, roperand, graph, output);
				return true;
			}
			else if(lconstant instanceof Double) {
				double value = ((Double) lconstant).doubleValue();
				this.build(expression, value, roperand, graph, output);
				return true;
			}
		}
		
		if(!(rconstant instanceof SymExpression)) {
			if(rconstant instanceof Boolean) {
				long value = ((Boolean) rconstant).booleanValue() ? 1: 0;
				this.build(expression, loperand, value, graph, output);
				return true;
			}
			else if(rconstant instanceof Long) {
				long value = ((Long) rconstant).longValue();
				this.build(expression, loperand, value, graph, output);
				return true;
			}
			else if(rconstant instanceof Double) {
				double value = ((Double) rconstant).doubleValue();
				this.build(expression, loperand, value, graph, output);
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
		
		constraint = StateEvaluation.smaller_tn(loperand, roperand);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, constraint);
		output.put(graph.get_error_set().set_bool(expression, true), constraints);
		
		constraint = StateEvaluation.greater_tn(loperand, roperand);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, constraint);
		output.put(graph.get_error_set().set_bool(expression, false), constraints);
		
		return true;
	}

}
