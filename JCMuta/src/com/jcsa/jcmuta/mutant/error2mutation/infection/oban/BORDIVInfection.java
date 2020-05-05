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
 * loperand == 0					--> set_numb(0)
 * roperand == 0					--> failure()
 * loperand == roperand				--> set_numb(1)
 * loperand != 1 or roperand != 1	--> chg_numb(x)
 * @author yukimula
 *
 */
public class BORDIVInfection extends OPRTInfection {

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
		Object rconstant = StateEvaluation.get_constant_value(roperand);
		
		if(!(lconstant instanceof SymExpression)) {
			if(lconstant instanceof Boolean) {
				if(!((Boolean) lconstant).booleanValue()) {
					output.put(graph.get_error_set().set_numb(expression, 0L), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
			else if(lconstant instanceof Long) {
				if(((Long) lconstant).longValue() == 0L) {
					output.put(graph.get_error_set().set_numb(expression, 0L), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
		}
		
		if(!(rconstant instanceof SymExpression)) {
			if(rconstant instanceof Boolean) {
				if(!((Boolean) rconstant).booleanValue()) {
					output.put(graph.get_error_set().failure(), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
			else if(rconstant instanceof Long) {
				if(((Long) rconstant).longValue() == 0L) {
					output.put(graph.get_error_set().failure(), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
		}
		
		return false;	/** unable to decide the mutation partially **/
	}

	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression lcondition, rcondition; StateConstraints constraints;
		CirStatement statement = expression.statement_of();
		
		/** x == 0 --> set_numb(0) **/
		lcondition = StateEvaluation.equal_with(loperand, 0L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, lcondition);
		output.put(graph.get_error_set().set_numb(expression, 0L), constraints);
		
		/** y = 0 --> failure() **/
		rcondition = StateEvaluation.equal_with(roperand, 0L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, rcondition);
		output.put(graph.get_error_set().failure(), constraints);
		
		/** x == y --> set_numb(1) **/
		lcondition = StateEvaluation.equal_with(loperand, roperand);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, lcondition);
		output.put(graph.get_error_set().set_numb(expression, 1L), constraints);
		
		/** x != 1 or y != 1 --> chg_numb **/
		lcondition = StateEvaluation.not_equals(loperand, 1L);
		rcondition = StateEvaluation.not_equals(roperand, 1L);
		constraints = StateEvaluation.get_disjunctions();
		this.add_constraint(constraints, statement, lcondition);
		this.add_constraint(constraints, statement, rcondition);
		output.put(graph.get_error_set().chg_numb(expression), constraints);
		
		return true;
	}

}
