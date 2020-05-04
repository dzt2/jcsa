package com.jcsa.jcmuta.mutant.error2mutation.infection.oabn;

import java.util.Map;

import com.jcsa.jcmuta.mutant.error2mutation.StateError;
import com.jcsa.jcmuta.mutant.error2mutation.StateErrorGraph;
import com.jcsa.jcmuta.mutant.error2mutation.StateEvaluation;
import com.jcsa.jcmuta.mutant.error2mutation.StateInfection;
import com.jcsa.jcmuta.mutant.error2mutation.infection.OPRTInfection;
import com.jcsa.jcparse.lang.ctype.CType;
import com.jcsa.jcparse.lang.ctype.CTypeAnalyzer;
import com.jcsa.jcparse.lang.irlang.expr.CirExpression;
import com.jcsa.jcparse.lang.lexical.COperator;
import com.jcsa.jcparse.lang.symb.StateConstraints;
import com.jcsa.jcparse.lang.symb.SymExpression;

/**
 * loperand == 0	--> set_numb(0)
 * roperand == 0	--> equivalence
 * 
 * @author yukimula
 *
 */
public class ADDLSHInfection extends OPRTInfection {

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
				if(!((Boolean) lconstant).booleanValue()) {
					output.put(graph.get_error_set().set_numb(expression, 0), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
			else if(lconstant instanceof Long) {
				if(((Long) lconstant).longValue() == 0) {
					output.put(graph.get_error_set().set_numb(expression, 0), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
		}
		
		if(!(rconstant instanceof SymExpression)) {
			if(rconstant instanceof Boolean) {
				if(!((Boolean) rconstant).booleanValue()) {
					return true;	/** equivalent mutants **/
				}
				else {
					output.put(graph.get_error_set().chg_numb(expression), 
							StateEvaluation.get_conjunctions()); return true;
				}
			}
			else if(rconstant instanceof Long) {
				long value = ((Long) rconstant).longValue();
				if(value < 0) {
					output.put(graph.get_error_set().chg_numb(expression), 
							StateEvaluation.get_conjunctions()); return true;
				}
				else if(value == 0) {
					return true;	/** equivalent mutants **/
				}
				else if(value >= StateInfection.max_bitwise) {
					output.put(graph.get_error_set().set_numb(expression, 0), 
							StateEvaluation.get_conjunctions()); return true;
				}
				else {
					output.put(graph.get_error_set().chg_numb(expression), 
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
		
		constraint = StateEvaluation.equal_with(loperand, 0);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), constraint);
		output.put(graph.get_error_set().set_numb(expression, 0L), constraints);
		
		constraint = StateEvaluation.not_equals(roperand, 0L);
		constraints = StateEvaluation.get_conjunctions();
		this.add_constraint(constraints, expression.statement_of(), constraint);
		output.put(graph.get_error_set().chg_numb(expression), constraints);
		
		return true;
	}

}
