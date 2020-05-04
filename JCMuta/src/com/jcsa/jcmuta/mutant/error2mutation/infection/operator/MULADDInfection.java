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
 * (*, +): 
 * 	loperand == 1 or roperand == 1 --> dif_numb(1)
 * 	loperand != 0 or roperand != 0 --> chg_numb(x)
 * @author yukimula
 *
 */
public class MULADDInfection extends OPRTInfection {

	@Override
	protected SymExpression muta_expression(CirExpression expression, CirExpression loperand, CirExpression roperand)
			throws Exception {
		return StateEvaluation.binary_expression(expression.
				get_data_type(), COperator.arith_add, loperand, roperand);
	}
	
	@Override
	protected boolean partial_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		Object lconstant = StateEvaluation.get_constant_value(loperand);
		Object rconstant = StateEvaluation.get_constant_value(roperand);
		
		/* lconstant == 0 || lconstant == 1 */
		if(!(lconstant instanceof SymExpression)) {
			if(lconstant instanceof Boolean) {
				if(((Boolean) lconstant).booleanValue()) {
					output.put(graph.get_error_set().dif_numb(expression, 1L), StateEvaluation.get_conjunctions());
					return true;
				}
			}
			else if(lconstant instanceof Long) {
				if(((Long) lconstant).longValue() == 1) {
					output.put(graph.get_error_set().dif_numb(expression, 1L), StateEvaluation.get_conjunctions());
					return true;
				}
			}
			else if(lconstant instanceof Double) {
				if(((Double) lconstant).doubleValue() == 1) {
					output.put(graph.get_error_set().dif_numb(expression, 1L), StateEvaluation.get_conjunctions());
					return true;
				}
			}
		}
		
		/* rconstant == 0 || rconstant == 1 */
		if(!(rconstant instanceof SymExpression)) {
			if(rconstant instanceof Boolean) {
				if(((Boolean) rconstant).booleanValue()) {
					output.put(graph.get_error_set().dif_numb(expression, 1L), StateEvaluation.get_conjunctions());
					return true;
				}
			}
			else if(rconstant instanceof Long) {
				if(((Long) rconstant).longValue() == 1) {
					output.put(graph.get_error_set().dif_numb(expression, 1L), StateEvaluation.get_conjunctions());
					return true;
				}
			}
			else if(rconstant instanceof Double) {
				if(((Double) rconstant).doubleValue() == 1) {
					output.put(graph.get_error_set().dif_numb(expression, 1L), StateEvaluation.get_conjunctions());
					return true;
				}
			}
		}
		
		return false;	/** undecidable partially **/
	}
	
	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lconstraint, rconstraint; StateConstraints constraints;
		
		lconstraint = StateEvaluation.not_equals(loperand, 0L);
		rconstraint = StateEvaluation.not_equals(roperand, 0L);
		constraints = StateEvaluation.get_disjunctions();
		this.add_constraint(constraints, expression.statement_of(), lconstraint);
		this.add_constraint(constraints, expression.statement_of(), rconstraint);
		output.put(graph.get_error_set().chg_numb(expression), constraints);
		
		return true;
	}
	
}
