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
 * ropernad == 0	--> failure()
 * loperand == k * roperand	--> set_numb(x)
 * loperand != 0	--> chg_numb(x)
 * @author yukimula
 *
 */
public class LSHMODInfection extends OPRTInfection {

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
		
		if(!(lconstant instanceof SymExpression)) {
			if(lconstant instanceof Boolean) {
				if(!((Boolean) lconstant).booleanValue()) {
					return true;	/** equivalent mutant **/
				}
			}
			else if(lconstant instanceof Long) {
				if(((Long) lconstant).longValue() == 0L) {
					return true;	/** equivalent mutant **/
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
		
		return false;
	}

	@Override
	protected boolean symbolic_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		SymExpression constraint; StateConstraints constraints;
		CirStatement statement = expression.statement_of();
		
		constraint = StateEvaluation.equal_with(roperand, 0L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, constraint);
		output.put(graph.get_error_set().failure(), constraints);
		
		constraint = StateEvaluation.not_equals(loperand, 0L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, statement, constraint);
		output.put(graph.get_error_set().chg_numb(expression), constraints);
		
		SymExpression yk = StateEvaluation.multiply_expression(expression.get_data_type(), roperand);
		constraint = StateEvaluation.equal_with(StateEvaluation.get_symbol(loperand), yk);
		this.add_constraint(constraints, statement, constraint);
		output.put(graph.get_error_set().set_numb(expression, 0L), constraints);
		
		return true;
	}

}
