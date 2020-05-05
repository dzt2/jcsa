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
 * loperand == 0 or roperand == 0	--> equivalence
 * loperand == -1 or roperand == -1	--> rsv_numb(x)
 * loperand & roperand != 0			--> chg_numb(x)
 * @author yukimula
 *
 */
public class BXRADDInfection extends OPRTInfection {

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
		
		/** lconstant == 0 or lconstant == -1 **/
		if(!(lconstant instanceof SymExpression)) {
			if(lconstant instanceof Boolean) {
				if(!((Boolean) lconstant).booleanValue()) {
					return true;	/** equivalent mutants **/
				}
			}
			else if(lconstant instanceof Long) {
				if(((Long) lconstant).longValue() == 0L) {
					return true;	/** equivalent mutants **/
				}
				else if(((Long) lconstant).longValue() == -1L) {
					output.put(graph.get_error_set().rsv_numb(expression), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
		}
		
		/** rconstant == 0 or rconstant == -1 **/
		if(!(rconstant instanceof SymExpression)) {
			if(rconstant instanceof Boolean) {
				if(!((Boolean) rconstant).booleanValue()) {
					return true;	/** equivalent mutants **/
				}
			}
			else if(rconstant instanceof Long) {
				if(((Long) rconstant).longValue() == 0L) {
					return true;	/** equivalent mutants **/
				}
				else if(((Long) rconstant).longValue() == -1L) {
					output.put(graph.get_error_set().rsv_numb(expression), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
		}
		
		return false;
	}

	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lcondition, rcondition; StateConstraints constraints;
		CirStatement statement = expression.statement_of();
		
		/** loperand & roperand != 0 **/
		rcondition = StateEvaluation.
				binary_expression(expression.get_data_type(), COperator.bit_and, loperand, roperand);
		lcondition = StateEvaluation.not_equals(rcondition, StateEvaluation.new_constant(0L));
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, lcondition);
		output.put(graph.get_error_set().chg_numb(expression), constraints);
		
		/** loperand == -1 or roperand == -1 **/
		lcondition = StateEvaluation.equal_with(loperand, -1L);
		rcondition = StateEvaluation.equal_with(roperand, -1L);
		constraints = StateEvaluation.get_disjunctions();
		this.add_constraint(constraints, statement, lcondition);
		this.add_constraint(constraints, statement, rcondition);
		output.put(graph.get_error_set().rsv_numb(expression), constraints);
		
		return true;
	}

}
