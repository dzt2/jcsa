package com.jcsa.jcmuta.mutant.error2mutation.infection.operator;

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
 * roperand != 0
 * @author yukimula
 *
 */
public class ADDSUBInfection extends OPRTInfection {

	@Override
	protected SymExpression muta_expression(CirExpression expression, CirExpression loperand, CirExpression roperand)
			throws Exception {
		return StateEvaluation.binary_expression(expression.
				get_data_type(), COperator.arith_sub, loperand, roperand);
	}

	@Override
	protected boolean partial_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		Object rconstant = StateEvaluation.get_constant_value(roperand);
		
		if(!(rconstant instanceof SymExpression)) {
			if(rconstant instanceof Boolean) {
				if(((Boolean) rconstant).booleanValue()) {
					output.put(graph.get_error_set().dif_numb(expression, -2L), 
							StateEvaluation.get_conjunctions());
				}
			}
			else if(rconstant instanceof Long) {
				long value = ((Long) rconstant).longValue();
				if(value != 0) {
					output.put(graph.get_error_set().dif_numb(expression, -2 * value), 
							StateEvaluation.get_conjunctions());
				}
			}
			else if(rconstant instanceof Double) {
				double value = ((Double) rconstant).doubleValue();
				if(value != 0) {
					output.put(graph.get_error_set().dif_numb(expression, -2 * value), 
							StateEvaluation.get_conjunctions());
				}
			}
			else if(rconstant instanceof String) {
				long value = 0;
				if(!rconstant.toString().equals(StateEvaluation.NullPointer)) {
					value = this.random_address();
				}
				
				if(value != 0) {
					output.put(graph.get_error_set().dif_numb(expression, -2 * value), 
							StateEvaluation.get_conjunctions());
				}
			}
			return true;
		}
		else {
			return false;	/* unable to decide partially */
		}
	}
	
	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression constraint; StateConstraints constraints;
		constraint = StateEvaluation.not_equals(roperand, 0L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), constraint);
		output.put(graph.get_error_set().chg_numb(expression), constraints);
		return true;
	}

}
