package com.jcsa.jcmuta.mutant.error2mutation.infection.oban;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.infection.OPRTInfection;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

/**
 * loperand == 0	--> equivalence
 * loperand == 1|-1	--> set_numb(1)
 * roperand == 0	--> failure()
 * roperand == 1|-1	--> set_numb(0)
 * loperand = k * roperand	--> set_numb(0)
 * loperand != 0	--> chg_numb(x)
 * @author yukimula
 *
 */
public class BANMODInfection extends OPRTInfection {

	@Override
	protected SymExpression muta_expression(CirExpression expression, CirExpression loperand, CirExpression roperand)
			throws Exception {
		return StateEvaluation.binary_expression(expression.
				get_data_type(), COperator.arith_mod, loperand, roperand);
	}

	@Override
	protected boolean partial_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		Object lconstant = StateEvaluation.get_constant_value(loperand);
		Object rconstant = StateEvaluation.get_constant_value(roperand);
		
		/** lconstant == 0 || lconstant == 1 || lconstant == -1 **/
		if(!(lconstant instanceof SymExpression)) {
			if(lconstant instanceof Boolean) {
				if(((Boolean) lconstant).booleanValue()) {
					output.put(graph.get_error_set().set_numb(expression, 1L), 
							StateEvaluation.get_conjunctions()); return true;
				}
				else {
					return true;	/** equivalent mutant detected **/
				}
			}
			else if(lconstant instanceof Long) {
				if(((Long) lconstant).longValue() == 0L) {
					return true;	/** equivalent mutant detected **/
				}
				else if(((Long) lconstant).longValue() == 1L) {
					output.put(graph.get_error_set().set_numb(expression, 1L), 
							StateEvaluation.get_conjunctions()); return true;
				}
				else if(((Long) lconstant).longValue() == -1L) {
					output.put(graph.get_error_set().set_numb(expression, 1L), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
		}
		
		/** roperand == 0 or roperand == 1 or -1 **/
		if(!(rconstant instanceof SymExpression)) {
			if(rconstant instanceof Boolean) {
				if(((Boolean) rconstant).booleanValue()) {
					output.put(graph.get_error_set().set_numb(expression, 0L), 
							StateEvaluation.get_conjunctions()); return true;
				}
				else {
					output.put(graph.get_error_set().failure(), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
			else if(rconstant instanceof Long) {
				if(((Long) rconstant).longValue() == 0L) {
					output.put(graph.get_error_set().failure(), 
							StateEvaluation.get_conjunctions()); return true;
				}
				else if(((Long) rconstant).longValue() == 1L) {
					output.put(graph.get_error_set().set_numb(expression, 0L), 
							StateEvaluation.get_conjunctions()); return true;
				}
				else if(((Long) rconstant).longValue() == -1L) {
					output.put(graph.get_error_set().set_numb(expression, 0L), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
		}
		
		return true;
	}

	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lcondition, rcondition; StateConstraints constraints;
		CirStatement statement = expression.statement_of();
		
		/** roperand == 0 **/
		lcondition = StateEvaluation.equal_with(roperand, 0L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, lcondition);
		output.put(graph.get_error_set().failure(), constraints);
		
		/** loperand = k * roperand **/
		rcondition = StateEvaluation.multiply_expression(expression.get_data_type(), roperand);
		lcondition = StateEvaluation.equal_with(StateEvaluation.get_symbol(loperand), rcondition);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, lcondition);
		output.put(graph.get_error_set().set_numb(expression, 0L), constraints);
		
		/** loperand != 0 --> chg_numb(x) **/
		lcondition = StateEvaluation.not_equals(loperand, 0L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, lcondition);
		output.put(graph.get_error_set().chg_numb(expression), constraints);
		
		return true;
	}

}
