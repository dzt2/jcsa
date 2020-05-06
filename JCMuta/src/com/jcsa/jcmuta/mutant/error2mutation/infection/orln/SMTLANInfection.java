package com.jcsa.jcmuta.mutant.error2mutation.infection.orln;

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
 * (1) x == 0	{0 < y}	--> {true, false}
 * (2) y == 0	{x < 0}	--> {true, false}
 * (3) otherwise{x >= y}--> {false, true}
 * @author yukimula
 *
 */
public class SMTLANInfection extends OPRTInfection {
	
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
			if(StateEvaluation.is_zero_number(lconstant)) {
				constraint = StateEvaluation.greater_tn(roperand, 0L);
				constraints = StateEvaluation.get_conjunctions();
				this.add_constraint(constraints, statement, constraint);
				output.put(graph.get_error_set().set_bool(expression, false), constraints);
			}
			else {
				constraint = StateEvaluation.greater_eq(loperand, roperand);
				constraints = StateEvaluation.get_conjunctions();
				this.add_constraint(constraints, statement, constraint);
				output.put(graph.get_error_set().set_bool(expression, true), constraints);
			}
			return true;
		}
		
		if(!(rconstant instanceof SymExpression)) {
			if(StateEvaluation.is_zero_number(rconstant)) {
				constraint = StateEvaluation.smaller_tn(loperand, 0L);
				constraints = StateEvaluation.get_conjunctions();
				this.add_constraint(constraints, statement, constraint);
				output.put(graph.get_error_set().set_bool(expression, false), constraints);
			}
			else {
				constraint = StateEvaluation.greater_eq(loperand, roperand);
				constraints = StateEvaluation.get_conjunctions();
				this.add_constraint(constraints, statement, constraint);
				output.put(graph.get_error_set().set_bool(expression, true), constraints);
			}
			return true;
		}
		
		return false;
	}
	
	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lcondition, rcondition; StateConstraints constraints;
		CirStatement statement = expression.statement_of();
		
		/** (loperand == 0, roperand > 0) or (loperand < 0, roperand == 0) **/
		lcondition = StateEvaluation.logic_and(StateEvaluation.equal_with(loperand, 0L), StateEvaluation.greater_tn(roperand, 0L));
		rcondition = StateEvaluation.logic_and(StateEvaluation.smaller_tn(loperand, 0L), StateEvaluation.equal_with(roperand, 0L));
		constraints = StateEvaluation.get_disjunctions();
		this.add_constraint(constraints, statement, lcondition); 
		this.add_constraint(constraints, statement, rcondition);
		output.put(graph.get_error_set().set_bool(expression, false), constraints);
		
		/** (loperand != 0 and roperand != 0 and loperand >= roperand) **/
		lcondition = StateEvaluation.not_equals(loperand, 0L);
		rcondition = StateEvaluation.not_equals(roperand, 0L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, lcondition); 
		this.add_constraint(constraints, statement, rcondition);
		this.add_constraint(constraints, statement, StateEvaluation.greater_eq(loperand, roperand));
		output.put(graph.get_error_set().set_bool(expression, true), constraints);
		
		return true;
	}
	
}
