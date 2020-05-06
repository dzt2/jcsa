package com.jcsa.jcmuta.mutant.error2mutation.infection.obln;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.infection.OPRTInfection;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

/**
 * loperand > 1 or loperand < 0
 * roperand > 1 or roperand < 0
 * @author yukimula
 *
 */
public class BORLORInfection extends OPRTInfection {

	@Override
	protected SymExpression muta_expression(CirExpression expression, CirExpression loperand, CirExpression roperand)
			throws Exception {
		return StateEvaluation.logic_or(loperand, roperand);
	}

	@Override
	protected boolean partial_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		StateConstraints constraints; SymExpression constraint;
		Object lconstant = StateEvaluation.get_constant_value(loperand);
		Object rconstant = StateEvaluation.get_constant_value(roperand);
		CirStatement statement = expression.statement_of();
		
		if(lconstant instanceof Long) {
			if(((Long) lconstant).longValue() > 1) {
				constraint = StateEvaluation.not_equals(roperand, 0L);
				constraints = StateEvaluation.get_conjunctions();
				this.add_constraint(constraints, statement, constraint);
				output.put(graph.get_error_set().set_bool(expression, true), constraints);
			}
			else if(((Long) lconstant).longValue() < 0) {
				constraint = StateEvaluation.not_equals(roperand, 0L);
				constraints = StateEvaluation.get_conjunctions();
				this.add_constraint(constraints, statement, constraint);
				output.put(graph.get_error_set().set_bool(expression, true), constraints);
			}
			else if(((Long) lconstant).longValue() == 1) {
				constraints = StateEvaluation.get_disjunctions();
				this.add_constraint(constraints, statement, StateEvaluation.greater_tn(roperand, 1L));
				this.add_constraint(constraints, statement, StateEvaluation.smaller_tn(roperand, 0L));
				output.put(graph.get_error_set().set_bool(expression, true), constraints);
			}
			return true;
		}
		
		if(rconstant instanceof Long) {
			if(((Long) rconstant).longValue() > 1) {
				constraint = StateEvaluation.not_equals(loperand, 0L);
				constraints = StateEvaluation.get_conjunctions();
				this.add_constraint(constraints, statement, constraint);
				output.put(graph.get_error_set().set_bool(expression, true), constraints);
			}
			else if(((Long) rconstant).longValue() < 0) {
				constraint = StateEvaluation.not_equals(loperand, 0L);
				constraints = StateEvaluation.get_conjunctions();
				this.add_constraint(constraints, statement, constraint);
				output.put(graph.get_error_set().set_bool(expression, true), constraints);
			}
			else if(((Long) rconstant).longValue() == 1) {
				constraints = StateEvaluation.get_disjunctions();
				this.add_constraint(constraints, statement, StateEvaluation.greater_tn(loperand, 1L));
				this.add_constraint(constraints, statement, StateEvaluation.smaller_tn(loperand, 0L));
				output.put(graph.get_error_set().set_bool(expression, true), constraints);
			}
		}
		
		return false;
	}

	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		StateConstraints constraints = StateEvaluation.get_disjunctions();
		CirStatement statement = expression.statement_of();
		
		this.add_constraint(constraints, statement, StateEvaluation.greater_tn(loperand, 1L));
		this.add_constraint(constraints, statement, StateEvaluation.smaller_tn(loperand, 0L));
		this.add_constraint(constraints, statement, StateEvaluation.greater_tn(roperand, 1L));
		this.add_constraint(constraints, statement, StateEvaluation.smaller_tn(roperand, 0L));
		output.put(graph.get_error_set().set_bool(expression, true), constraints);
		
		return true;
	}

}
