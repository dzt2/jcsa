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

/**
 * --> chg_numb(x)
 * @author yukimula
 *
 */
public class MODADDInfection extends OPRTInfection {

	@Override
	protected SymExpression muta_expression(CirExpression expression, CirExpression loperand, CirExpression roperand)
			throws Exception {
		return StateEvaluation.binary_expression(expression.
				get_data_type(), COperator.arith_add, loperand, roperand);
	}

	@Override
	protected boolean partial_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		return false;
	}

	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		/* x < -2 * y --> chg_numb(x) */
		SymExpression constraint; StateConstraints constraints;
		SymExpression y2 = StateEvaluation.multiply_expression(expression.get_data_type(), roperand, -2);
		constraint = StateEvaluation.smaller_tn(StateEvaluation.get_symbol(loperand), y2);
		constraints = StateEvaluation.get_conjunctions(); 
		constraints.add_constraint(expression.statement_of(), constraint);
		output.put(graph.get_error_set().chg_numb(expression), StateEvaluation.get_conjunctions());
		return true;
	}

}
