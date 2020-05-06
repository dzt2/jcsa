package com.jcsa.jcmuta.mutant.error2mutation.infection.oaln;

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
 * loperand != 0 and loperand == k * roperand --> set_true
 * 
 * @author yukimula
 *
 */
public class MODLANInfection extends OPRTInfection {

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
			if(StateEvaluation.get_condition_value(lconstant)) {
				constraint = StateEvaluation.equal_with(StateEvaluation.get_symbol(loperand), 
						StateEvaluation.multiply_expression(expression.get_data_type(), roperand));
				constraints = StateEvaluation.get_conjunctions();
				this.add_constraint(constraints, statement, constraint);
				output.put(graph.get_error_set().set_bool(expression, true), constraints);
			}
			return true;
		}
		
		if(!(rconstant instanceof SymExpression)) {
			constraint = StateEvaluation.equal_with(StateEvaluation.get_symbol(loperand), 
					StateEvaluation.multiply_expression(expression.get_data_type(), roperand));
			constraints = StateEvaluation.get_conjunctions();
			this.add_constraint(constraints, statement, StateEvaluation.not_equals(loperand, 0L));
			this.add_constraint(constraints, statement, constraint);
			output.put(graph.get_error_set().set_bool(expression, true), constraints);
			return true;
		}
		
		return false;
	}

	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lcondition, rcondition; StateConstraints constraints;
		CirStatement statement = expression.statement_of();
		
		lcondition = StateEvaluation.not_equals(loperand, 0L);
		rcondition = StateEvaluation.equal_with(StateEvaluation.get_symbol(loperand), 
				StateEvaluation.multiply_expression(expression.get_data_type(), roperand));
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, lcondition);
		this.add_constraint(constraints, statement, rcondition);
		output.put(graph.get_error_set().set_bool(expression, true), constraints);
		
		return true;
	}

}
