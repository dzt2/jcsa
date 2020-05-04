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
 * roperand == 1 --> dif_numb(-1)
 * otherwise     --> chg_numb(x)
 * @author yukimula
 *
 */
public class DIVSUBInfection extends OPRTInfection {

	@Override
	protected SymExpression muta_expression(CirExpression expression, CirExpression loperand, CirExpression roperand)
			throws Exception {
		return StateEvaluation.binary_expression(expression.get_data_type(), COperator.arith_sub, loperand, roperand);
	}

	@Override
	protected boolean partial_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		Object rconstant = StateEvaluation.get_constant_value(roperand);
		if(!(rconstant instanceof SymExpression)) {
			if(rconstant instanceof Boolean) {
				output.put(graph.get_error_set().dif_numb(expression, -1L), StateEvaluation.get_conjunctions());
				return true;
			}
			else if(rconstant instanceof Long) {
				if(((Long) rconstant).longValue() == 1L) {
					output.put(graph.get_error_set().dif_numb(expression, -1L), StateEvaluation.get_conjunctions());
					return true;
				}
			}
			else if(rconstant instanceof Double) {
				if(((Double) rconstant).doubleValue() == 1.0) {
					output.put(graph.get_error_set().dif_numb(expression, -1L), StateEvaluation.get_conjunctions());
					return true;
				}
			}
		}
		
		/** unable to decide it partially **/	return false;
	}

	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression constraint; StateConstraints constraints;
		
		constraint = StateEvaluation.equal_with(roperand, 1L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), constraint);
		output.put(graph.get_error_set().dif_numb(expression, -1L), constraints);
		
		output.put(graph.get_error_set().chg_numb(expression), StateEvaluation.get_conjunctions());
		
		return true;
	}

}
