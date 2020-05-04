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
 * roperand == 0
 * loperand != 0 and roperand != 1 and roperand != -1
 * @author yukimula
 *
 */
public class MULDIVInfection extends OPRTInfection {

	@Override
	protected SymExpression muta_expression(CirExpression expression, CirExpression loperand, CirExpression roperand)
			throws Exception {
		return StateEvaluation.binary_expression(expression.get_data_type(), COperator.arith_div, loperand, roperand);
	}

	@Override
	protected boolean partial_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		Object lconstant = StateEvaluation.get_constant_value(loperand);
		Object rconstant = StateEvaluation.get_constant_value(roperand);
		
		/* loperand != 0 */
		if(!(lconstant instanceof SymExpression)) {
			if(lconstant instanceof Boolean) {
				if(!(((Boolean) lconstant).booleanValue())) {
					return true;	/** equivalent mutant as detected **/
				}
			}
			else if(lconstant instanceof Long) {
				if(((Long) lconstant).longValue() == 0) {
					return true;	/** equivalent mutant as detected **/
				}
			}
			else if(lconstant instanceof Double) {
				if(((Double) lconstant).doubleValue() == 0) {
					return true;	/** equivalent mutant as detected **/
				}
			}
		}
		
		/* roperand == 0 | roperand = 1 or -1 */
		if(!(rconstant instanceof SymExpression)) {
			if(rconstant instanceof Boolean) {
				if(((Boolean) rconstant).booleanValue()) {
					return true;	/** equivalent mutant as detected **/
				}
				else {
					output.put(graph.get_error_set().failure(), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
			else if(rconstant instanceof Long) {
				if(((Long) rconstant).longValue() == 1) {
					return true;	/** equivalent mutant as detected **/
				}
				else if(((Long) rconstant).longValue() == -1) {
					return true;	/** equivalent mutant as detected **/
				}
				else if(((Long) rconstant).longValue() == 0) {
					output.put(graph.get_error_set().failure(), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
			else if(rconstant instanceof Double) {
				if(((Double) rconstant).doubleValue() == 1) {
					return true;	/** equivalent mutant as detected **/
				}
				else if(((Double) rconstant).doubleValue() == -1) {
					return true;	/** equivalent mutant as detected **/
				}
				else if(((Double) rconstant).doubleValue() == 0) {
					output.put(graph.get_error_set().failure(), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
		}
		
		return false;
	}

	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lcondition, mcondition, rcondition; StateConstraints constraints;
		
		lcondition = StateEvaluation.equal_with(roperand, 0L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), lcondition);
		output.put(graph.get_error_set().failure(), constraints);
		
		lcondition = StateEvaluation.not_equals(loperand, 0);
		mcondition = StateEvaluation.not_equals(roperand, 1);
		rcondition = StateEvaluation.not_equals(roperand, -1);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), lcondition);
		this.add_constraint(constraints, expression.statement_of(), mcondition);
		this.add_constraint(constraints, expression.statement_of(), rcondition);
		output.put(graph.get_error_set().chg_numb(expression), constraints);
		
		return true;
	}

}
