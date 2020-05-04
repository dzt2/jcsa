package com.jcsa.jcmuta.mutant.error2mutation.infection.operator;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.infection.OPRTInfection;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

/**
 * roperand == 0
 * [loperand == 0 	equivalent]
 * [loperand == 1 or loperand == -1]
 * [roperand == 1 or roperand == -1]
 * loperand == k * roperand
 * loperand != 0
 * @author yukimula
 *
 */
public class MULMODInfection extends OPRTInfection {

	@Override
	protected SymExpression muta_expression(CirExpression expression, CirExpression loperand, CirExpression roperand)
			throws Exception {
		CType type = CTypeAnalyzer.get_value_type(expression.get_data_type());
		
		if(CTypeAnalyzer.is_boolean(type) || CTypeAnalyzer.is_integer(type)) {
			return StateEvaluation.binary_expression(expression.
					get_data_type(), COperator.arith_mod, loperand, roperand);
		}
		else { 	return null; 	/* invalid type returns null */ }
	}

	@Override
	protected boolean partial_evaluate(CirExpression expression, CirExpression loperand, CirExpression roperand,
			StateErrorGraph graph, Map<StateError, StateConstraints> output) throws Exception {
		Object lconstant = StateEvaluation.get_constant_value(loperand);
		Object rconstant = StateEvaluation.get_constant_value(roperand);
		
		if(!(lconstant instanceof SymExpression)) {
			if(lconstant instanceof Boolean) {
				if(((Boolean) lconstant).booleanValue()) {
					output.put(graph.get_error_set().set_numb(expression, 1), 
							StateEvaluation.get_conjunctions()); return true;
				}
				else {
					return true;	/** equivalent mutant **/
				}
			}
			else if(lconstant instanceof Long) {
				if(((Long) lconstant).longValue() == 1) {
					output.put(graph.get_error_set().set_numb(expression, 1), 
							StateEvaluation.get_conjunctions()); return true;
				}
				else if(((Long) lconstant).longValue() == -1) {
					output.put(graph.get_error_set().set_numb(expression, 1), 
							StateEvaluation.get_conjunctions()); return true;
				}
				else if(((Long) lconstant).longValue() == 0) {
					return true;	/** equivalent mutant **/
				}
			}
		}
		
		if(!(rconstant instanceof SymExpression)) {
			if(rconstant instanceof Boolean) {
				if(((Boolean) rconstant).booleanValue()) {
					output.put(graph.get_error_set().set_numb(expression, 0), 
							StateEvaluation.get_conjunctions()); return true;
				}
				else {
					output.put(graph.get_error_set().failure(), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
			else if(rconstant instanceof Long) {
				if(((Long) rconstant).longValue() == 1) {
					output.put(graph.get_error_set().set_numb(expression, 0), 
							StateEvaluation.get_conjunctions()); return true;
				}
				else if(((Long) rconstant).longValue() == -1) {
					output.put(graph.get_error_set().set_numb(expression, 0), 
							StateEvaluation.get_conjunctions()); return true;
				}
				else if(((Long) rconstant).longValue() == 0) {
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
		
		constraint = StateEvaluation.equal_with(roperand, 0L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), constraint);
		output.put(graph.get_error_set().failure(), constraints);
		
		SymExpression yk = StateEvaluation.multiply_expression(expression.get_data_type(), roperand);
		constraint = StateEvaluation.equal_with(StateEvaluation.get_symbol(loperand), yk);
		this.add_constraint(constraints, expression.statement_of(), constraint);
		output.put(graph.get_error_set().set_numb(expression, 0), constraints);
		
		constraint = StateEvaluation.not_equals(loperand, 0);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), constraint);
		output.put(graph.get_error_set().chg_numb(expression), constraints);
		
		return true;
	}

}
