package com.jcsa.jcmuta.mutant.error2mutation.infection.oaan;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.infection.OPRTInfection;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

public class MODDIVInfection extends OPRTInfection {

	@Override
	protected SymExpression muta_expression(CirExpression expression, CirExpression loperand, CirExpression roperand)
			throws Exception {
		return StateEvaluation.binary_expression(expression.
				get_data_type(), COperator.arith_div, loperand, roperand);
	}

	@Override
	protected boolean partial_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		Object lconstant = StateEvaluation.get_constant_value(loperand);
		
		if(!(lconstant instanceof SymExpression)) {
			if(lconstant instanceof Boolean) {
				if(!((Boolean) lconstant).booleanValue()) {
					/** equivalent mutant **/	return true;
				}
			}
			else if(lconstant instanceof Long) {
				if(((Long) lconstant).longValue() == 0) {
					/** equivalent mutant **/	return true;
				}
			}
		}
		
		return false;
	}

	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression constraint; StateConstraints constraints;
		
		/* x != 0 --> chg_numb */
		constraint = StateEvaluation.not_equals(loperand, 0);
		constraints = StateEvaluation.get_conjunctions(); 
		constraints.add_constraint(expression.statement_of(), constraint);
		output.put(graph.get_error_set().chg_numb(expression), constraints);
		
		return true;
	}

}
